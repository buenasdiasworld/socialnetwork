package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.LikeRequest;
import main.data.response.base.Response;
import main.data.response.type.LikesWithUsers;
import main.exception.apierror.ApiError;
import main.service.LikesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Api(tags = {SpringFoxConfig.LIKE_TAG})
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class LikesController {
    private final LikesService likesService;

    @ApiOperation(value = "Ставил ли лайк пользователь на пост/комментарий")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping("/liked")
    public ResponseEntity<Response<LikesWithUsers>> isLiked(
            @RequestParam(name = "user_id", required = false) Integer userId,
            @RequestParam(name = "item_id") int itemId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(likesService.isLiked(userId, itemId, type));
    }

    @ApiOperation(value = "Получить лайки к посту")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @GetMapping("/likes")
    public ResponseEntity<Response<LikesWithUsers>> getLikes(
            @RequestParam(name = "item_id") int itemId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(likesService.getLikes(itemId, type));
    }

    @ApiOperation(value = "Поставить лайк")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @PutMapping("/likes")
    public ResponseEntity<Response<LikesWithUsers>> setLike(
            @RequestBody LikeRequest request
    ) {
        return ResponseEntity.ok(likesService.setLike(request));
    }

    @ApiOperation(value = "Удалить лайк")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = ""),
            @ApiResponse(code = 401, message = "unauthorized", response = ApiError.class)
    })
    @DeleteMapping("/likes")
    public ResponseEntity<Response<LikesWithUsers>> deleteLike(
            @RequestParam(name = "item_id") int itemId,
            @RequestParam String type
    ) {
        return ResponseEntity.ok(likesService.deleteLike(itemId, type));
    }
}
