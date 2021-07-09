package main.controller;

import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.PostRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.PostInResponse;
import main.service.PostServiceImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
@Api(tags = { SpringFoxConfig.PROFILE_TAG })
@Controller
@AllArgsConstructor
@RequestMapping("/api/v1/users")
public class ProfileController {
    private final PostServiceImpl postServiceImpl;

    @PostMapping("/{id}/wall")
    public ResponseEntity<Response<PostInResponse>> addPostToWall(
            @PathVariable int id,
            @RequestBody PostRequest request,
            @RequestParam(name = "publish_date", required = false) Long pubDate
            ){
        return ResponseEntity.ok(postServiceImpl.addNewPost(id, request, pubDate));
    }

    @GetMapping("/{id}/wall")
    public ResponseEntity<ListResponse<PostInResponse>> showPersonWall(
        @PathVariable int id,
        @RequestParam(defaultValue = "0") int offset,
        @RequestParam(defaultValue = "20") int itemsPerPage
    ){
        return ResponseEntity.ok(postServiceImpl.showWall(id, offset, itemsPerPage));
    }

}
