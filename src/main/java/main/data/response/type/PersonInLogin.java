package main.data.response.type;

import lombok.Data;
import main.model.Person;

@Data
public class PersonInLogin extends PersonProfile {
    private String token;

    public PersonInLogin(Person person) {
        super(person);
    }
}
