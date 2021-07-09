package main.controller;

import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.NotificationSettingsRequest;
import main.data.request.PasswordRecoveryRequest;
import main.data.request.PasswordSetRequest;
import main.data.request.RegistrationRequest;
import main.data.response.NotificationSettingsResponse;
import main.data.response.RegistrationResponse;
import main.data.response.base.Response;
import main.data.response.type.InfoInResponse;
import main.exception.apierror.ApiError;
import main.service.NotificationService;
import main.service.PasswordService;
import main.service.RegistrationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@Api(tags = {SpringFoxConfig.ACCOUNT_TAG})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/account")
public class ApiAccountController {

    private final PasswordService passwordService;
    private final RegistrationService registrationService;
    private final NotificationService notificationService;

    @Value("${linkToChange.password}")
    public String passwordChangeLink;
    @Value("${linkToChange.email}")
    public String emailChangeLink;

    @Value("${reCaptcha.secretCode}")
    public String secretCode;
    @Value("${reCaptcha.url}")
    public String captchaUrl;

    @ApiOperation(value = "Получение настроек оповещения", notes = "Получает настройки оповещения")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping("/notifications")
    public ResponseEntity<Response<Set<NotificationSettingsResponse>>> getListOfNotifications() {
        return ResponseEntity.ok(notificationService.getSettings());
    }


    @ApiOperation(value = "Редактирование настроек оповещения", notes = "Редактирование настроек оповещения")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная смена статуса"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping("/notifications")
    public ResponseEntity<Response<InfoInResponse>> setNotifications(
            @ApiParam(name = "Request body")
            @RequestBody NotificationSettingsRequest request) {
        return ResponseEntity.ok(notificationService.set(request));
    }


    @ApiOperation(value = "Восстановление пароля", notes = "Для незалогиненного пользователя.\n" +
            "Высылает ссылку для восстановления на почтовый ящик.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный запрос"),
            @ApiResponse(code = 400, message = "Такой email не зарегистрирован", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping(value = "/password/recovery")
    public ResponseEntity<Response<InfoInResponse>> recovery(
            @ApiParam(name = "Request body")
            @RequestBody PasswordRecoveryRequest request) {
        return ResponseEntity.ok(passwordService.restorePassword(request, passwordChangeLink));
    }


    @ApiOperation(value = "Смена пароля", notes = "Для залогиненного пользователя.\n" +
            "Высылает ссылку для изменения пароля на почтовый ящик.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный запрос"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping(value = "/password/change")
    public ResponseEntity<Response<InfoInResponse>> sendLinkToChangePassword() {
        return ResponseEntity.ok(passwordService.changePassOrEmail("пароля", passwordChangeLink));
    }


    @ApiOperation(value = "Установка пароля", notes = "Сохраняет новый пароль в БД")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная смена пароля"),
            @ApiResponse(code = 400, message = "Аутентификация не пройдена", response = ApiError.class)
    })
    @PutMapping(value = "/password/set")
    public ResponseEntity<Response<InfoInResponse>> setPassword(
            @ApiParam(type = "header", name = "Referer")
            @RequestHeader(name = "Referer") String referer,
            @ApiParam(name = "Request body")
            @RequestBody PasswordSetRequest request) {
        return ResponseEntity.ok(passwordService.setPassword(request, referer));
    }


    @ApiOperation(value = "Смена email", notes = "Для залогиненного пользователя.\n" +
            "Высылает ссылку для изменения email на действующий почтовый ящик.")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешный запрос"),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping(value = "/email/change")
    public ResponseEntity<Response<InfoInResponse>> sendLinkToChangeEmail() {
        return ResponseEntity.ok(passwordService.changePassOrEmail("email", emailChangeLink));
    }


    @ApiOperation(value = "Установка email", notes = "Сохраняет новый email в БД")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная смена email"),
            @ApiResponse(code = 400, message = "Такой email уже зарегистрирован в сети", response = ApiError.class)
    })
    @PutMapping(value = "/email")
    public ResponseEntity<Response<InfoInResponse>> setEmail(
            @ApiParam(name = "Request body")
            @RequestBody PasswordRecoveryRequest request) {
        return ResponseEntity.ok(passwordService.setEmail(request));
    }


    @ApiOperation(value = "Регистрация", notes = "Регистрация нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная регистрация"),
            @ApiResponse(code = 400, message = "invalid_request", response = ApiError.class)
    })
    @PostMapping(value = "/register")
    public ResponseEntity<RegistrationResponse> registration(
            @ApiParam(name = "Request body")
            @RequestBody RegistrationRequest request) {
        return ResponseEntity.ok(registrationService.registrationNewPerson(request, secretCode, captchaUrl));
    }
}
