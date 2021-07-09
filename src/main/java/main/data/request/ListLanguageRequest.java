package main.data.request;

import lombok.Data;

@Data
public class ListLanguageRequest {
    private String language;
    private int offset;
    private int itemPerPage;
}
