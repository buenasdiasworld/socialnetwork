package main.data.response.type;

import lombok.Data;
import main.model.Country;

@Data
public class CountryList {
    private int id;
    private String title;

    public CountryList(Country country) {
        id = country.getId();
        title = country.getTitle();
    }
}
