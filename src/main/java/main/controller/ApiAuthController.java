package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.LoginRequest;
import main.data.response.CaptchaResponse;
import main.data.response.base.Response;
import main.data.response.type.PersonInLogin;
import main.data.response.type.ResponseMessage;
import main.exception.apierror.ApiError;
import main.service.CaptchaServiceImpl;
import main.service.PersonServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = { SpringFoxConfig.AUTH_TAG })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class ApiAuthController {
    private final PersonServiceImpl userService;

    @ApiOperation(value = "Авторизация в системе")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное авторизация и выдача JWT токена"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PostMapping("/login")
    public ResponseEntity<Response<PersonInLogin>> login(
            @Parameter(in = ParameterIn.PATH, name = "request", description = "E-mail и пароль")
            @RequestBody
                    LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }

    @ApiOperation(value = "Выход из системы")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный выход"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PostMapping("/logout")
    public ResponseEntity<Response<ResponseMessage>> logout() {
        return ResponseEntity.ok(userService.logout());
    }
}
