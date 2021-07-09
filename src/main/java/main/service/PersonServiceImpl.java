package main.service;

import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.core.auth.JwtUtils;
import main.data.PersonPrincipal;
import main.data.request.LoginRequest;
import main.data.request.MeProfileRequest;
import main.data.response.base.Response;
import main.data.response.type.*;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.*;
import main.repository.BlocksBetweenUsersRepository;
import main.repository.CityRepository;
import main.repository.CountryRepository;
import main.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PersonServiceImpl implements UserDetailsService, PersonService {

    private final PersonRepository personRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CityRepository cityRepository;
    private final CountryRepository countryRepository;
    private final BlocksBetweenUsersRepository blocksBetweenUsersRepository;
    private PostService postService;

    @Autowired
    public void setPostService(@Lazy PostService postService) {
        this.postService = postService;
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        Optional<Person> user = personRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UsernameNotFoundException(email);
        }
        return new PersonPrincipal(user.get());
    }

    @Override
    public Response<PersonInLogin> login(LoginRequest request) {
        Authentication authentication
                = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        PersonPrincipal personPrincipal = (PersonPrincipal) authentication.getPrincipal();

        PersonInLogin personInLogin = new PersonInLogin(personPrincipal.getPerson());
        personInLogin.setToken(jwt);

        Person currentPerson = personPrincipal.getPerson();
        currentPerson.setLastOnlineTime(Instant.now());

        personRepository.save(currentPerson);

        return new Response<>(personInLogin);
    }

    @Override
    public Person loginTelegram(long chatId) {
        Optional<Person> optionalPerson = personRepository.findByTelegramId(chatId);
        return optionalPerson.orElse(null);
    }

    @Override
    public Response<ResponseMessage> logout() {
        return new Response<>(new ResponseMessage("ok"));
    }

    @Override
    public Response<MeProfile> getMe() {
        int id = ContextUtilities.getCurrentUserId();
        if (id == 0) {
            throw new BadRequestException(new ApiError(
                    "не авторизован"
            ));
        }
        Person person = getById(id);
        if (person == null) {
            throw new BadRequestException(new ApiError(
                    "нет аккаунта с таким айди"
            ));
        }

        Response<MeProfile> response = new Response<>();
        MeProfile profile = new MeProfile(person);
        response.setData(profile);
        return response;
    }

    @Override
    public Response<MeProfile> putMe(MeProfileRequest updatedCurrentPerson) {
        Person personUpdated = personRepository.findById(ContextUtilities.getCurrentUserId());

        if (personUpdated == null) {
            throw new BadRequestException(new ApiError(
                    "нет аккаунта с таким айди"
            ));
        }

        personUpdated.setLastName(updatedCurrentPerson.getLastName());
        personUpdated.setFirstName(updatedCurrentPerson.getFirstName());
        personUpdated.setBirthDate(updatedCurrentPerson.getBirthDate());
        personUpdated.setPhone(updatedCurrentPerson.getPhone());
        personUpdated.setAbout(updatedCurrentPerson.getAbout());

        Country countryUpdated = countryRepository.findById(updatedCurrentPerson.getCountry());
        City cityUpdated = cityRepository.findById(updatedCurrentPerson.getCity());

        personUpdated.setCountry(countryUpdated);
        personUpdated.setCity(cityUpdated);

        personRepository.save(personUpdated);

        MeProfile updatedPerson = new MeProfile(personUpdated);
        Response<MeProfile> response = new Response<>();
        response.setData(updatedPerson);
        return response;
    }

    @Override
    public Response<InfoInResponse> deleteMe() {

        int id = ContextUtilities.getCurrentUserId();

        if (id == 0) {
            throw new BadRequestException(new ApiError(
                    "не авторизован"
            ));
        }

        Person personToDetele = personRepository.findById(id);

        List<Post> postsToDelete = postService.findByAuthor(personToDetele);

        if (!postsToDelete.isEmpty()) {
            postsToDelete.forEach(p -> postService.delPost(p.getId()));
        }

        personToDetele.setAbout(personToDetele.getFirstName() + " " + personToDetele.getLastName()
                + " решил удалить свою страницу");

        personToDetele.setPhone("");
        personToDetele.setDeleted(true);

        // изменить емейл и пароль чтобы пользователь не смог зайти в удаленную учетку

        personToDetele.setEmail(id + "DeletedId" + UUID.randomUUID().toString());
        personToDetele.setPasswordHash(UUID.randomUUID().toString());

        personToDetele.setPhotoURL("/static/img/page_deleted.jpg");

        personRepository.save(personToDetele);

        InfoInResponse info = new InfoInResponse("ok");
        Response<InfoInResponse> response = new Response<>();
        response.setData(info);
        return response;
    }

    @Override
    public Person getById(int personId) {
        return personRepository.findById(personId);
    }

    public boolean isAuthenticated() {
        if (!SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            throw new UsernameNotFoundException(ApiError.INVALID_REQUEST);
        }
        return true;
    }

    public Person getAuthUser() {
        isAuthenticated();
        return ContextUtilities.getCurrentPerson();
    }

    public Person checkAuthUser(int id) {
        Person person = getAuthUser();
        if (person.getId() != id) {
            throw new UsernameNotFoundException(ApiError.INVALID_REQUEST);
        }
        return person;
    }

    public Response<MeProfile> getProfile(Integer id) {
        Person person;
        Optional<Person> personOpt = personRepository.findById(id);
        if (personOpt.isPresent()) {
            person = personOpt.get();
        } else {
            throw new UsernameNotFoundException(ApiError.INVALID_REQUEST);
        }
        // проверка удален ли профиль
        if (person.isDeleted()) {
            throw new BadRequestException(
                    new ApiError("profile deleted", person.getFirstName() + " " + person.getLastName()
                            + " решил удалить свой профиль"));
        }
        //Проверка на блокировку профиля
        BlocksBetweenUsers blocksBetweenUsers = blocksBetweenUsersRepository
                .findBySrc_IdAndDst_Id(id, ContextUtilities.getCurrentUserId());
        if (blocksBetweenUsers != null) {

            person.setFirstName("Доступ к профилю ограничен");
            person.setLastName("пользователем");
        }
        //Проверка на блокировку от текущего профиля
        blocksBetweenUsers = blocksBetweenUsersRepository
                .findBySrc_IdAndDst_Id(ContextUtilities.getCurrentUserId(), id);
        person.setBlocked(blocksBetweenUsers != null);
        Response<MeProfile> response = new Response<>();

        MeProfile profile = new MeProfile(person);
        response.setData(profile);
        return response;
    }

    @Override
    public Person save(Person person) {
        return personRepository.save(person);
    }

    public Response<DataMessage> unblockUser(int id) {
        int currentUserId = ContextUtilities.getCurrentUserId();
        Response<DataMessage> response = new Response<>();
        response.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        response.setError("");
        BlocksBetweenUsers blocksBetweenUsers = blocksBetweenUsersRepository
                .findBySrc_IdAndDst_Id(currentUserId, id);
        if (blocksBetweenUsers != null) {
            blocksBetweenUsersRepository.delete(blocksBetweenUsers);
            response.setData(new DataMessage("ок"));
            return response;
        }
        response.setData(new DataMessage("Профиль уже разблокирован"));
        return response;
    }

    public Response<DataMessage> blockUser(int id) {
        int currentUserId = ContextUtilities.getCurrentUserId();
        Response<DataMessage> response = new Response<>();
        response.setTimestamp(LocalDateTime.now().toEpochSecond(ZoneOffset.UTC));
        response.setError("");
        if ((blocksBetweenUsersRepository.findBySrc_IdAndDst_Id(currentUserId, id)) == null) {
            BlocksBetweenUsers blocksBetweenUsers = new BlocksBetweenUsers();
            blocksBetweenUsers.setDst(personRepository.findById(id));
            blocksBetweenUsers.setSrc(personRepository.findById(currentUserId));
            blocksBetweenUsersRepository.save(blocksBetweenUsers);
            response.setData(new DataMessage("ок"));
            return response;
        }
        response.setData(new DataMessage("Профиль уже заблокирован"));
        return response;
    }

    private void setToBlocked(Person person) {
        person.setFirstName("Доступ заблокирован");
        person.setBirthDate(null);
        person.setCity(new City());
        person.setCountry(new Country());
        person.setPhone("");
        person.setLastName("");
        person.setAbout("");
    }
}

