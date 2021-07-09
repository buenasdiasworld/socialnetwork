package main.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DialogMessageRequest {
    @JsonProperty("message_text")
    private String messageText;
}
