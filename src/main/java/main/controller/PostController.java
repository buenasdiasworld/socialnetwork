package main.controller;

import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.PostRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.PostDelete;
import main.data.response.type.PostInResponse;
import main.exception.apierror.ApiError;
import main.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Api(tags = {SpringFoxConfig.POST_TAG})
@Controller
@AllArgsConstructor
@RequestMapping("/api/v1")
public class PostController {
    private final PostServiceImpl postServiceImpl;

    @ApiOperation(value = "Показать все посты")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное получение постов"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping(value = "/feeds", produces = "application/json")
    public ResponseEntity<ListResponse<PostInResponse>> getFeeds(
            @RequestParam(required = false, defaultValue = "") String name,
            @RequestParam(required = false, defaultValue = "0") int offset,
            @RequestParam(required = false, defaultValue = "10") int itemPerPage
    ) {
        return ResponseEntity.ok(postServiceImpl.getFeeds(name, offset, itemPerPage));
    }

    @ApiOperation(value = "Удалить пост по ID")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное удаление поста"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @DeleteMapping(value = "/post/{id}", produces = "application/json")
    public ResponseEntity<Response<PostDelete>> deletePost(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID поста",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable Integer id) {
        return ResponseEntity.ok(postServiceImpl.delPost(id));
    }

    @ApiOperation(value = "Редактирование поста")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешное редактирование поста"),
            @ApiResponse(code = 400, message = "Bad Request", response = ApiError.class),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping(value = "/post/{id}", produces = "application/json")
    public ResponseEntity<Response<PostInResponse>> editPost(
            @Parameter(in = ParameterIn.PATH, name = "id", description = "ID поста",
                    schema = @Schema(
                            type = "integer",
                            format = "int64"
                    )) @PathVariable Integer id,
            @RequestParam(name = "publish_date", required = false) Long pubDate,
            @ApiParam(name = "Request body")
            @RequestBody PostRequest request
    ) {
        return ResponseEntity.ok(postServiceImpl.editPost(id, pubDate, request));
    }
}
