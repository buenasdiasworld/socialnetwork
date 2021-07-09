package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Notification;
import main.model.NotificationReadStatusCode;
import main.model.NotificationTypeCode;
import main.model.Person;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private int id;

    @JsonProperty(value = "sent_time")
    private long sentTime;

    @JsonProperty(value = "event_type")
    private NotificationTypeCode code;

    @JsonProperty(value = "entity_author")
    private PersonInLogin entityAuthor;

    private String info;

    @JsonProperty(value = "read_status")
    private NotificationReadStatusCode readStatus;

    public NotificationResponse(Notification notification, String info, Person author) {
        id = notification.getId();
        sentTime = notification.getSentTime().toEpochMilli();
        code = notification.getType().getCode();
        entityAuthor = new PersonInLogin(author);
        this.info = info;
        readStatus = notification.getReadStatus();
    }
}
