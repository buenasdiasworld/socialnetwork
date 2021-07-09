package main.data.response.type;

import lombok.Data;
import main.model.City;

@Data
public class CityList {
    private int id;
    private String title;

    public CityList(City city) {
        id = city.getId();
        title = city.getTitle();
    }
}
