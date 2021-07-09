package main.controller;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.CommentRequest;
import main.data.response.CommentResponse;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.CommentInResponse;
import main.data.response.type.ItemDelete;
import main.exception.apierror.ApiError;
import main.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = { SpringFoxConfig.COMMENT_TAG })
@AllArgsConstructor
@Controller
@RequestMapping("/api/v1/post")
public class CommentController {
    private final CommentService commentService;

    @ApiOperation(value = "Создать комментарий для публикации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное создание комментария"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PostMapping(value = "/{id}/comments", produces = "application/json")
    public ResponseEntity<CommentResponse> createComment(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID поста",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable Integer id,
            @ApiParam(name = "Request body")
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.createComment(id, request));
    }

    @ApiOperation(value = "Показать комментарии для публикации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение комментариев"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping(value = "/{id}/comments", produces = "application/json")
    public ResponseEntity<ListResponse<CommentInResponse>> showPostComments(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID поста",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable Integer id,
            @RequestParam(required = true, defaultValue = "0") Integer offset,
            @RequestParam(required = true, defaultValue = "20") Integer itemPerPage
    ) {
        return ResponseEntity.ok(commentService.getPostComments(id, offset, itemPerPage));
    }

    @ApiOperation(value = "Редактировать комментарий для публикации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное редактирование комментария"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping(value = "/{id}/comments/{comment_id}", produces = "application/json")
    public ResponseEntity<CommentResponse> editPostComment(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID поста",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable Integer id,
            @Parameter(in = ParameterIn.PATH, name = "comment_id", description = "ID комментария",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable(value = "comment_id") Integer commentId,
            @ApiParam(name = "Request body")
            @RequestBody CommentRequest request
    ) {
        return ResponseEntity.ok(commentService.editComment(id, commentId, request));
    }

    @ApiOperation(value = "Удалить комментарий для публикации")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное удаление комментария"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @DeleteMapping(value = "{id}/comments/{comment_id}", produces = "application/json")
    public ResponseEntity<Response<ItemDelete>> deletePostComment(
            @PathVariable Integer id,
            @PathVariable(value = "comment_id") Integer commentId
    ) {
        return ResponseEntity.ok(commentService.deleteComment(id, commentId));
    }

}
