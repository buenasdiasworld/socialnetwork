package main.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LikeRequest {
    @JsonProperty(value = "item_id")
    int itemId;
    String type;
}
