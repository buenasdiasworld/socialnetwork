package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class InfoInResponse {

  @JsonProperty("message")
  private String messagetext;

  public InfoInResponse(String messagetext) {
    this.messagetext = messagetext;
  }

}
