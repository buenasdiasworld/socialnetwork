package main.service;

import lombok.AllArgsConstructor;
import main.core.ContextUtilities;
import main.core.OffsetPageRequest;
import main.data.response.base.ListResponse;
import main.data.response.type.CommentInResponse;
import main.data.response.type.MeProfile;
import main.data.response.type.PostInResponse;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.model.*;
import main.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@AllArgsConstructor
public class SearchService {

    private final PersonRepository personRepository;
    private final CountryRepository countryRepository;
    private final CityRepository cityRepository;
    private final PostRepository postRepository;
    private final CommentServiceImpl commentService;
    private final TagRepository tagRepository;
    private final BlocksBetweenUsersRepository blocksBetweenUsersRepository;

    public ListResponse<MeProfile> searchPerson(String firstName, String lastName,
                                                Integer ageFrom,
                                                Integer ageTo, String country,
                                                String city, Integer offset, Integer itemPerPage) {

        int id = ContextUtilities.getCurrentUserId();
        if (id == 0) {
            throw new BadRequestException(new ApiError("Не авторизован"));
        }

        Pageable pageable = new OffsetPageRequest(offset, itemPerPage, Sort.unsorted());

        List<MeProfile> searchResult = new ArrayList<>();

        Page<Person> resultPage;

        Integer countryId = null;
        Set<Integer> cityIds = new HashSet<>();
        Date ageFromToDate = null;
        Date ageToToDate = null;

        if (country != null) {

            Optional<Country> countryOptional = countryRepository.findByTitle(country);

            if (countryOptional.isEmpty()) {

                return new ListResponse<>(searchResult, 0,
                        offset,
                        itemPerPage);
            }
            countryId = countryOptional.get().getId();
        }
        if (city != null) {

            //города в мире
            if (country == null) {

                List<Optional<City>> cityOptional = cityRepository.findByTitle(city);

                if (cityOptional.isEmpty()) {
                    return new ListResponse<>(searchResult, 0,
                            offset,
                            itemPerPage);
                }

                cityOptional.forEach(c -> cityIds.add(c.get().getId()));
            }
            //города в стране
            else {

                List<Optional<City>> cityOptional = cityRepository.findByTitleAndCountryId(city, countryId);

                if (cityOptional.isEmpty()) {
                    return new ListResponse<>(searchResult, 0,
                            offset,
                            itemPerPage);
                }

                cityOptional.forEach(c -> cityIds.add(c.get().getId()));

            }


        }
        if (ageFrom != null) {
            ageFromToDate = calculateBirthDateFromAge(ageFrom);
        }
        if (ageTo != null) {
            ageToToDate = calculateBirthDateFromAge(ageTo);
        }

        resultPage = personRepository
                .findPersonByNameLastNameAgeCityCountry(firstName, lastName, ageFromToDate, ageToToDate,
                        countryId, cityIds, pageable);

        int currentUserId = ContextUtilities.getCurrentUserId();
        resultPage.forEach(r -> {
            BlocksBetweenUsers blocksBetweenUsers = blocksBetweenUsersRepository
                    .findBySrc_IdAndDst_Id(currentUserId, r.getId());
            r.setBlocked(blocksBetweenUsers != null);
            searchResult.add(new MeProfile(r));
        });

        return new ListResponse<>(searchResult, resultPage.getTotalElements(),
                offset,
                itemPerPage);

    }

    public ListResponse<PostInResponse> searchPost(String text, Long dateFrom, Long dateTo,
                                                   String author, List<String> tags,
                                                   Integer offset, Integer itemPerPage) {

        Pageable pageable = new OffsetPageRequest(offset, itemPerPage, Sort.unsorted());

        List<PostInResponse> searchPostResult = new ArrayList<>();

        Page<Post> resultPostPage;
        Date from = null;
        Date to = null;
        Set<Integer> authorsIds = new HashSet<>();
        String textUpdated=null;
        if (text != null ) { textUpdated= "%" + text + "%";}

        Set<Integer> tagsIds = new HashSet<>();

        if (dateFrom != null && dateTo != null) {
            from = new Date(dateFrom);
            to = new Date(dateTo);
        }
        if (author != null) {
            List<Optional<Person>> authors = personRepository
                    .findByLastNameLikeOrFirstNameLike(author, author);

            if (authors.isEmpty()) {
                return new ListResponse<>(searchPostResult, 0, offset, itemPerPage);
            }

            Set<Integer> authorsIdsTemp = new HashSet<>();

            authors.forEach(a -> authorsIdsTemp.add(a.get().getId()));

            authorsIds = authorsIdsTemp;

        }
        if (tags != null) {

            if (!tags.isEmpty()) {

                List<Optional<Tag>> tagsFound = tagRepository.findTagsByTagNames(tags);

                if (tagsFound.isEmpty()) {
                    return new ListResponse<>(searchPostResult, 0, offset, itemPerPage);
                }

                Set<Integer> tagsIdsTemp = new HashSet<>();
                tagsFound.forEach(t -> tagsIdsTemp.add(t.get().getId()));
                tagsIds = tagsIdsTemp;

            }
        }

        if (tagsIds.isEmpty()) {
            resultPostPage = postRepository
                    .findByTextPeriodAuthorNoTags(textUpdated, from, to, authorsIds, pageable);
        } else {
            resultPostPage = postRepository
                    .findByTextPeriodAuthorTags(textUpdated, from, to, authorsIds, tagsIds, pageable);
        }

        List<CommentInResponse> comments = commentService.getCommentsList(resultPostPage.getContent());
        resultPostPage.forEach(
                p -> searchPostResult.add(new PostInResponse(p, comments, -1))); //TODO check necessity

        return new ListResponse<>(searchPostResult, resultPostPage.getTotalElements(), offset,
                itemPerPage);
    }

    private Date calculateBirthDateFromAge(int age) {

        LocalDate today = LocalDate.now();

        LocalDate from = today.minusYears(age);

        return Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());

    }
}
