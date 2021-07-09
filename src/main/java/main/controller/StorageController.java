package main.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.AllArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.response.base.Response;
import main.data.response.type.Storage;
import main.service.StorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {SpringFoxConfig.STORAGE_TAG})
@RestController
@AllArgsConstructor
public class StorageController {

    private final StorageService storageService;

    @ApiOperation(value = "Загрузка файла в хранилище")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Успешная загрузка файла")
    })
    @PostMapping(value = "/api/v1/storage", produces = "application/json")
    public ResponseEntity<Response<Storage>> upload(
            @Parameter(in = ParameterIn.QUERY, name = "file to upload", description = "загружаемый файл", example = "file")
            @RequestParam(value = "file")
                    MultipartFile file,
            @Parameter(name = "type", description = "тип файла", example = "IMAGE")
            @RequestParam(value = "type")
                    String type) {

        return ResponseEntity.ok(storageService.store(file, type));
    }


}
