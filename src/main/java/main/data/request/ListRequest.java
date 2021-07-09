package main.data.request;

import lombok.Data;

@Data
public class ListRequest {
    private String query;
    private int offset;
    private int itemPerPage;
}
