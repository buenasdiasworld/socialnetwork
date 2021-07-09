package main.data.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class DialogAddRequest {
    @JsonProperty("users_ids")
    private List<Integer> userIds;

    private String name;
}
