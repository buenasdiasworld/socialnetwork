package main.data.response.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.Instant;

@Data
public abstract class RecordResponse {
    private String error = "";
    @ApiModelProperty(value = "timestamp", example = "1559751301818")
    private long timestamp = Instant.now().toEpochMilli();
}
