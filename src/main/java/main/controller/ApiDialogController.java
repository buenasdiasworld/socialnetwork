package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.DialogAddRequest;
import main.data.request.DialogMessageRequest;
import main.data.request.ListRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.*;
import main.exception.apierror.ApiError;
import main.service.DialogServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = { SpringFoxConfig.DIALOG_TAG })
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/dialogs")
public class ApiDialogController {
    private final DialogServiceImpl dialogService;

    @ApiOperation(value = "Получение списка диалогов")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получены диалоги"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping
    public ResponseEntity<ListResponse<DialogList>> list(ListRequest request) {
        return ResponseEntity.ok(dialogService.list(request));
    }

    @ApiOperation(value = "Добавление диалога")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешно добавлен"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PostMapping
    public ResponseEntity<Response<DialogNew>> add(@RequestBody DialogAddRequest request) {
        return ResponseEntity.ok(dialogService.add(request));
    }

    @ApiOperation(value = "Отправить сообщение в диалог")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное отправлено"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PostMapping("/{dialogId}/messages")
    public ResponseEntity<Response<DialogMessage>> addMessage(
            @Parameter(in = ParameterIn.PATH, name = "dialogId", description = "ID диалога",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    ))
            @PathVariable int dialogId,
            @RequestBody DialogMessageRequest request
    ) {
        return ResponseEntity.ok(dialogService.addMessage(dialogId, request));
    }

    @ApiOperation(value = "Получение списка сообщений в диалоге")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получены сообщения"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping("/{dialogId}/messages")
    public ResponseEntity<ListResponse<DialogMessage>> listMessage(
            @Parameter(in = ParameterIn.PATH, name = "dialogId", description = "ID диалога",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    ))
            @PathVariable int dialogId,
            ListRequest request
    ) {
        return ResponseEntity.ok(dialogService.listMessage(dialogId, request));
    }

    @ApiOperation(value = "Количество непрочитанных сообщений пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получен ответ"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping("/unreaded")
    public ResponseEntity<Response<ResponseCount>> countUnreaded() {
        return ResponseEntity.ok(dialogService.countUnreadedMessage());
    }

    @ApiOperation(value = "Установить сообщение как прочитанное")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное выполнено"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping("/{dialogId}/messages/{messageId}/read")
    public ResponseEntity<Response<ResponseMessage>> setReadMessage(
            @Parameter(in = ParameterIn.PATH, name = "dialogId", description = "ID диалога",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    ))
            @PathVariable int dialogId,
            @Parameter(in = ParameterIn.PATH, name = "messageId", description = "ID сообщения",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    ))
            @PathVariable int messageId
    ) {
        return ResponseEntity.ok(dialogService.setReadMessage(messageId));
    }
}
