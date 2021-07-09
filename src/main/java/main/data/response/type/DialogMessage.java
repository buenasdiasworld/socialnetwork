package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.model.Message;
import main.model.ReadStatus;

@Data
@NoArgsConstructor
public class DialogMessage {
    private int id;
    private long time;

    @JsonProperty("author_id")
    private int authorId;

    private DialogMessagePerson author;

    @JsonProperty("recipient_id")
    private Integer recipientId;

    private DialogMessagePerson recipient;

    @JsonProperty("message_text")
    private String messageText;

    @JsonProperty("read_status")
    private ReadStatus readStatus;

    public DialogMessage(Message message) {
        this.id = message.getId();
        this.time = message.getTime().toEpochMilli();
        this.authorId = message.getAuthor().getId();
        this.author = new DialogMessagePerson(message.getAuthor());
        this.recipientId = (message.getRecipient() != null) ? message.getRecipient().getId() : null;
        this.recipient = (message.getRecipient() != null) ? new DialogMessagePerson(message.getRecipient()) : null;
        this.messageText = message.getMessageText();
        this.readStatus = message.getReadStatus();
    }
}
