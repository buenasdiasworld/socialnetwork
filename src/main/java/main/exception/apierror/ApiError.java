package main.exception.apierror;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(value = "ApiError")
@Data
public class ApiError implements Serializable {
    public static final String INVALID_REQUEST = "invalid_request";

    @ApiModelProperty(value = "error", example = "invalid_request")
    String error;
    @ApiModelProperty(value = "error_description", example = "string")
    @JsonProperty("error_description")
    String errorDescription;
    @ApiModelProperty(value = "status_text", example = "401")
    String statusText;

    public ApiError(String error, String errorDescription) {
        this.error = error;
        this.errorDescription = errorDescription;
        statusText = errorDescription;
    }

    public ApiError(String errorDescription) {
        this(INVALID_REQUEST, errorDescription);
    }

    public ApiError() {
        this("Bad request");
    }
}
