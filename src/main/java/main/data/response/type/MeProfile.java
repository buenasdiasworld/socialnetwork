package main.data.response.type;

import lombok.Data;
import main.model.Person;

@Data
public class MeProfile extends PersonProfile {
    private CityList city;
    private CountryList country;

    public MeProfile(Person person) {
        super(person);
        this.city = new CityList(person.getCity());
        this.country = new CountryList(person.getCountry());
    }
}



