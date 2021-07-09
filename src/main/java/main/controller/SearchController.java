package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.response.base.ListResponse;
import main.data.response.type.MeProfile;
import main.data.response.type.PostInResponse;
import main.exception.apierror.ApiError;
import main.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = {SpringFoxConfig.SEARCH_TAG})
@Controller
@AllArgsConstructor
public class SearchController {

    private final SearchService searchService;

    @ApiOperation(value = "Поиск пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение текущего пользователя"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping(value = "/api/v1/users/search", produces = "application/json")
    public ResponseEntity<ListResponse<MeProfile>> searchPerson(

            @Parameter(description = "имя пользователя", example = "Иван")
            @RequestParam(required = false, name = "first_name") String firstName,
            @Parameter(description = "фамилия пользователя", example = "Лосев")
            @RequestParam(required = false, name = "last_name") String lastName,
            @Parameter(description = "возраст пользователя от", example = "15")
            @RequestParam(required = false, name = "age_from") Integer ageFrom,
            @Parameter(description = "возраст пользователя до", example = "18")
            @RequestParam(required = false, name = "age_to") Integer ageTo,
            @Parameter(description = "страна пользователя", example = "Италия")
            @RequestParam(required = false) String country,
            @Parameter(description = "город пользователя", example = "Милан")
            @RequestParam(required = false) String city,
            @Parameter(description = "offset", example = "0")
            @RequestParam(required = false, defaultValue = "0") Integer offset,
            @Parameter(description = "элементов на станице", example = "10")
            @RequestParam(required = false, defaultValue = "10") Integer itemPerPage

    ) {

        return ResponseEntity.ok(searchService
                .searchPerson(firstName, lastName, ageFrom, ageTo, country, city, offset, itemPerPage));
    }

    @ApiOperation(value = "Поиск публикации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение публикации"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping(value = "/api/v1/post", produces = "application/json")
    public ResponseEntity<ListResponse<PostInResponse>> searchNews(
            @RequestParam(required = false, name = "text")
            @Parameter(description = "текст новости", example = "java") String text,
            @RequestParam(required = false, name = "date_from")
            @Parameter(description = "дата с") Long dateFrom,
            @RequestParam(required = false, name = "date_to")
            @Parameter(description = "дата до") Long dateTo,
            @RequestParam(required = false, name = "author")
            @Parameter(description = "автор", example = "Петр") String author,
            @RequestParam(required = false, name = "tags")
            @Parameter(description = "теги", example = "[\"lol\", \"IT\", \"post\"]") List<String> tags,
            @RequestParam(required = false, defaultValue = "0")
            @Parameter(description = "оффсет", example = "0") Integer offset,
            @RequestParam(required = false, defaultValue = "10")
            @Parameter(description = "элементов на странице", example = "11") Integer itemPerPage) {

        return ResponseEntity.ok(searchService.searchPost(text, dateFrom, dateTo, author, tags,
                offset, itemPerPage));
    }


}
