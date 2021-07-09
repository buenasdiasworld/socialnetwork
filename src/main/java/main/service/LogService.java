package main.service;

import main.data.response.base.Response;

import java.util.List;

public interface LogService {
    Response<List<String>> getLogs(String logType, String logLevel, Integer lineCount);
}
