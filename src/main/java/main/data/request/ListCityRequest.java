package main.data.request;

import lombok.Data;

@Data
public class ListCityRequest {
    private int countryId;
    private String city;
    private int offset;
    private int itemPerPage;
}
