package main.controller;

import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import main.config.SpringFoxConfig;
import main.data.response.base.Response;
import main.service.LogService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Api(tags = {SpringFoxConfig.LOG_TAG})
@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/log")
public class ApiLogController {
    private final LogService logService;

    @GetMapping("")
    public ResponseEntity<Response<List<String>>> getLogs(@RequestParam String logType,
                                                          @RequestParam String logLevel,
                                                          @RequestParam Integer lineCount) {
        return ResponseEntity.ok(logService.getLogs(logType, logLevel, lineCount));
    }
}
