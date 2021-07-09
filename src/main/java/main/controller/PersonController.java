package main.controller;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.MeProfileRequest;
import main.data.response.base.Response;
import main.data.response.type.InfoInResponse;
import main.data.response.type.MeProfile;
import main.exception.BadRequestException;
import main.exception.apierror.ApiError;
import main.service.PersonServiceImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {SpringFoxConfig.PROFILE_TAG})
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class PersonController {

    private final PersonServiceImpl personServiceImpl;

    @ApiOperation(value = "Получить текущего пользователя")

    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение текущего пользователя"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping(value = "/me", produces = "application/json")
    public ResponseEntity<Response<MeProfile>> getCurrentUser() {

        return ResponseEntity.ok(personServiceImpl.getMe());
    }


    @ApiOperation(value = "Редактирование текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное редактирование текущего пользователя"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping(value = "/me", produces = "application/json")
    public ResponseEntity<Response<MeProfile>> updateCurrentUser(
            @ApiParam(name = "Updated profile info")
            @RequestBody MeProfileRequest updatedCurrentUser) {

        return ResponseEntity.ok(personServiceImpl.putMe(updatedCurrentUser));

    }

    @ApiOperation(value = "Удаление текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное удаление текущего пользователя"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @DeleteMapping(value = "/me", produces = "application/json")
    public ResponseEntity<Response<InfoInResponse>> delete() {
        return ResponseEntity.ok(personServiceImpl.deleteMe());
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ApiOperation(value = "Получить пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение пользователя по id"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    public ResponseEntity<Response<MeProfile>> showPersonProfile(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID пользователя",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    ))
            @PathVariable int id
    ) {
        return ResponseEntity.ok(personServiceImpl.getProfile(id));
    }

    @PutMapping(value = "/block/{id}", produces = "application/json")
    @ApiOperation(value = "Блокировка пользователя по id", response = Response.class)
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная блокировка пользователя"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    public ResponseEntity blockUser(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID пользователя",
                    schema = @Schema(
                            type = "integer",
                            format = "int64",
                            example = "1"
                    )) @PathVariable int id) {
        Response response = new Response();
        try {
            response = personServiceImpl.blockUser(id);
        } catch (BadRequestException ex) {
            throw new BadRequestException(new ApiError("invalid_request", "Bad request"));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping(value = "/block/{id}", produces = "application/json")
    @ApiOperation(value = "Разблокировать пользователя по id", response = Response.class)
    @ApiResponse(code = 200, message = "Успешная разблокировка пользователя")
    public ResponseEntity unblockUser(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID пользователя",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable int id) {
        Response response = new Response();
        try {
            response = personServiceImpl.unblockUser(id);
        } catch (BadRequestException ex) {
            throw new BadRequestException(new ApiError("invalid_request", "Bad request"));
        }
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
