package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.MessagesPermission;
import main.model.Person;

@Data
@NoArgsConstructor
public class PersonProfile {
    private int id;

    @ApiModelProperty(value = "first_name", example = "Аркадий")
    @JsonProperty("first_name")
    private String firstName;

    @ApiModelProperty(value = "last_name", example = "Паровозов")
    @JsonProperty("last_name")
    private String lastName;

    @ApiModelProperty(value = "reg_date", example = "1559751301818")
    @JsonProperty("reg_date")
    private long regDate;

    @ApiModelProperty(value = "birth_date", example = "1559751401818")
    @JsonProperty("birth_date")
    private long birthDate;

    @ApiModelProperty(value = "email", example = "arkadiiP@pochta.ru")
    private String email;
    @ApiModelProperty(value = "phone", example = "89991112233")
    private String phone;
    @ApiModelProperty(value = "photo", example = "/img/213123.jpg")
    private String photo;
    @ApiModelProperty(value = "about", example = "Человек и паровоз")
    private String about;

    @JsonProperty("messages_permission")
    private MessagesPermission messagesPermission;

    @ApiModelProperty(value = "last_online_time", example = "1559751401818")
    @JsonProperty("last_online_time")
    private long lastOnlineTime;

    @ApiModelProperty(value = "is_blocked", example = "false")
    @JsonProperty("is_blocked")
    private boolean isBlocked;

    public PersonProfile(Person person) {
        this.firstName = person.getFirstName();
        this.lastName = person.getLastName();
        this.email = person.getEmail();
        this.isBlocked = person.isBlocked();
        this.messagesPermission = person.getMessagesPermission();
        this.about = person.getAbout();
        this.id = person.getId();
        this.photo = person.getPhotoURL();
        this.phone = person.getPhone();
        this.lastOnlineTime = person.getLastOnlineTime() != null ? person.getLastOnlineTime().toEpochMilli() : 0;
        this.regDate = person.getRegDate() != null ? person.getRegDate().toEpochMilli() : 0;
        this.birthDate = person.getBirthDate() != null ? person.getBirthDate().getTime() : 0;
    }
}
