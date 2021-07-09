package main.data.response.type;

import lombok.Data;
import main.model.Person;

@Data
public class PersonInDialog {
    private int id;
    private String fullName;
    private String photo;

    public PersonInDialog (Person person) {
        if (person != null) {
            id = person.getId();
            fullName = person.getLastName() + " " + person.getFirstName();
            photo = person.getPhotoURL();
        }
    }
}
