package main.service;

import lombok.RequiredArgsConstructor;
import main.data.response.base.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceImpl implements LogService {
    @Value("${log.path}")
    public String logPath;
    @Value("${spring.profiles.active}")
    public String profile;

    public static String lvlToString(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(s).insert(sb.length(), " ", 0, 5 - s.length()).append("]");
        return sb.toString();
    }

    @Override
    public Response<List<String>> getLogs(String logType, String logLevel, Integer lineCount) {
        List<String> logRecords = new ArrayList<>();
        if (!(logType.isEmpty() || logLevel.isEmpty() || lineCount == null)) {
            try (
                    FileInputStream fs = new FileInputStream(String.format("%s/%s/%s/%s.log", logPath, profile, logType, logType));
                    BufferedReader br = new BufferedReader(new InputStreamReader(fs))
            ) {
                String line;
                while ((line = br.readLine()) != null) {
                    String level = lvlToString(logLevel.toUpperCase());
                    if (line.contains(level)) {
                        logRecords.add(line);
                    }
                }
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
        return new Response<>(logRecords);
    }
}
