package main.service;

import lombok.RequiredArgsConstructor;
import main.core.ContextUtilities;
import main.core.auth.JwtUtils;
import main.data.PersonPrincipal;
import main.data.request.PasswordRecoveryRequest;
import main.data.request.PasswordSetRequest;
import main.data.response.base.Response;
import main.data.response.type.InfoInResponse;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.Person;
import main.repository.PersonRepository;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {
    private final PersonRepository personRepository;
    private final JavaMailSender emailSender;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final PersonService personService;

    private static final String INVALID_REQUEST = "invalid_request";

    //send link to restore password
    @Override
    public Response<InfoInResponse> restorePassword(PasswordRecoveryRequest request, String link) {
        Optional<Person> optionalPerson = personRepository.findByEmail(request.getEmail());
        if (optionalPerson.isEmpty()) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST,
                    "Такой email не зарегистрирован"));
        }
        Person person = optionalPerson.get();
        String confirmationCode = RandomStringUtils.randomAlphanumeric(45);
        person.setConfirmationCode(confirmationCode);
        personRepository.save(person);
        // send link by email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Ссылка на восстановление пароля на SocialNetwork (group 8)");
        message.setText(link + "?code=" + confirmationCode);
        emailSender.send(message);
        return new Response<>(new InfoInResponse("Успешный запрос"));
    }

    //set new password by restoring or changing
    @Override
    public Response<InfoInResponse> setPassword(PasswordSetRequest request, String referer) {
        Person person;
        //if password is restored (person is unauthenticated)
        if (SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString().equals("anonymousUser")) {
            person = personRepository.findByConfirmationCode(referer.split("=")[1]
            ).orElseThrow(() -> new BadRequestException(new ApiError(INVALID_REQUEST,
                    "Аутентификация не пройдена.")));
        }
        //if password is changed (person is authenticated)
        else {
            //check token validity
            if (jwtUtils.validateJwtToken(request.getToken())) {
                person = ((PersonPrincipal) SecurityContextHolder.getContext().
                        getAuthentication().getPrincipal()).getPerson();
            } else throw new BadRequestException(new ApiError(INVALID_REQUEST,
                    "Аутентификация не пройдена."));
        }
        person.setPasswordHash(encoder.encode(request.getPassword()));
        personRepository.save(person);

        return new Response<>(new InfoInResponse("Успешная смена пароля"));
    }

    //send link to change password or email address
    @Override
    public Response<InfoInResponse> changePassOrEmail(String subject, String link) {
        Person person = ContextUtilities.getCurrentPerson();

        String confirmationCode = RandomStringUtils.randomAlphanumeric(45);
        person.setConfirmationCode(confirmationCode);
        personRepository.save(person);
        // send link by email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(person.getEmail());
        message.setSubject("Ссылка на изменение ".concat(subject)
                .concat(" на SocialNetwork (group 8)"));
        message.setText(link + "?code=" + confirmationCode);
        emailSender.send(message);
        return new Response<>(new InfoInResponse("Успешный запрос"));
    }

    //set new email address
    @Override
    public Response<InfoInResponse> setEmail(PasswordRecoveryRequest request) {
        Person person = ContextUtilities.getCurrentPerson();

        if (personRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException(new ApiError(INVALID_REQUEST,
                    "Такой email уже зарегистрирован в сети"));
        }

        person.setEmail(request.getEmail());
        personRepository.save(person);
        // send confirmation to new email
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(request.getEmail());
        message.setSubject("Подтверждение изменения email на SocialNetwork (group 8)");
        message.setText("Ваш email успешно изменен.");
        emailSender.send(message);
        return new Response<>(new InfoInResponse("Успешная смена email"));
    }
}

