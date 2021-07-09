package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.response.base.ListResponse;
import main.data.response.type.NotificationResponse;
import main.exception.apierror.ApiError;
import main.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = { SpringFoxConfig.NOTIFICATION_TAG })
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class ApiNotificationsController {

    private final NotificationService notificationService;

    @ApiOperation(value = "Получить список уведомлений", notes = "Получить список уведомлений для текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 400, message = "invalid_request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping("/notifications")
    public ResponseEntity<ListResponse<NotificationResponse>> getListOfNotifications(
            @Parameter(name = "offset", description = "Отступ от начала списка",
                    schema = @Schema(
                            type = "integer",
                            format = "int64",
                            defaultValue = "0"))
            @RequestParam(required = false, defaultValue = "0") int offset,
            @Parameter(name = "itemPerPage", description = "Количество элементов на страницу",
                    schema = @Schema(
                            type = "integer",
                            format = "int64",
                            defaultValue = "20"))
            @RequestParam(required = false, defaultValue = "20") int itemPerPage) {
        return ResponseEntity.ok(notificationService.list(offset, itemPerPage, false));
    }


    @ApiOperation(value = "Прочитать уведомление", notes = "Отметить уведомление как \"прочитанное\"")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 400, message = "invalid_request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping("/notifications")
    public ResponseEntity<ListResponse<NotificationResponse>> readNotification(
            @Parameter(name = "id", description = "ID уведомления",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"))
            @RequestParam(required = false, defaultValue = "0") int id,
            @Parameter(name = "all", description = "Пометка что прочесть все уведомления",
                    schema = @Schema(type = "boolean"))
            @RequestParam boolean all) {
        return ResponseEntity.ok(notificationService.read(id, all));
    }
}
