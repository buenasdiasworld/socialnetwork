package main.data.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationSettingsResponse {
    @JsonProperty(value = "type")
    private String notificationType;
    private boolean enable;
}
