package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.model.Person;

@Data
public class DialogMessagePerson {
    private int id;
    @JsonProperty("first_name")
    private String firstName;
    @JsonProperty("last_name")
    private String lastName;

    private String photo;

    private long lastOnlineTime;

    public DialogMessagePerson(Person person) {
        this.id = person.getId();
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.photo = person.getPhotoURL();
        this.lastOnlineTime = person.getLastOnlineTime().toEpochMilli();
    }
}
