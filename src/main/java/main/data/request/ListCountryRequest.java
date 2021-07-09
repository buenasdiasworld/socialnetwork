package main.data.request;

import lombok.Data;

@Data
public class ListCountryRequest {
    private String country;
    private int offset;
    private int itemPerPage;
}
