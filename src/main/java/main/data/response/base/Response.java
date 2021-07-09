package main.data.response.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@ApiModel(value = "Response")
public class Response<T> {

    private String error = "";
    @ApiModelProperty(value = "timestamp", example = "1559751301818")
    private long timestamp = Instant.now().toEpochMilli();
    @ApiModelProperty(value = "data", example = "{ "
        + "message: ok"
        + "}")
    private T data;

    public Response(T data) {
        this.data = data;
    }
}
