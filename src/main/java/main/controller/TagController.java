package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.request.TagRequest;
import main.data.response.base.ListResponse;
import main.data.response.base.Response;
import main.data.response.type.DataMessage;
import main.data.response.type.SingleTag;
import main.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
@Api(tags = { SpringFoxConfig.TAG_TAG })
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;

    @ApiOperation(value = "Получение тэгов к посту")
    @GetMapping("/")
    public ResponseEntity<ListResponse<SingleTag>> getPostTags(@RequestParam String tag,
                                                               @RequestParam int offset,
                                                               @RequestParam(required = false, defaultValue = "20") int itemsPerPage) {
        return ResponseEntity.ok(tagService.getPostTags(tag, offset, itemsPerPage));
    }

    @ApiOperation(value = "Создфть новый тэг")
    @PostMapping("/")
    public ResponseEntity<Response<SingleTag>> createTag(@RequestBody TagRequest request) {
        return ResponseEntity.ok(tagService.createTag(request));
    }

    @ApiOperation(value = "Удалить тэг")
    @DeleteMapping("/")
    public ResponseEntity<Response<DataMessage>> deleteTag(@RequestParam int id) {
        return ResponseEntity.ok(tagService.deleteTag(id));
    }
}
