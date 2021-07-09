package main.data.request;

import lombok.Data;

@Data
public class ChangePassRequest {
    private String token;
    private String password;
}
