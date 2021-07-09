package main.data.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import main.data.response.type.DataMessage;

@Data
@AllArgsConstructor
public class RegistrationResponse {
    String error;
    long timestamp;
    DataMessage data;
}
