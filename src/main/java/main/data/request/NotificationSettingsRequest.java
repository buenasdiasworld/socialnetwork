package main.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NotificationSettingsRequest {

  @JsonProperty("notification_type")
  private String notificationType;

  private Boolean enable;


}



