package main.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.exception.apierror.ApiError;

@Data
@AllArgsConstructor
public class BadRequestException extends RuntimeException {
    private final ApiError error;
}
