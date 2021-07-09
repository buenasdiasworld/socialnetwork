package main.data.response.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import main.model.Dialog;

import java.util.ArrayList;
import java.util.List;

@Data
public class DialogList {
    private int id;

    @JsonProperty("unread_count")
    private long unreadCount;

    @JsonProperty("last_message")
    private DialogMessage lastMessage;

    private String name;

    private boolean isGroupDialog;

    private List<PersonInDialog> persons;

    public DialogList(Dialog dialog) {
        id = dialog.getId();
        name = dialog.getName();

        isGroupDialog = dialog.getPersons().size() != 2;

        persons = new ArrayList<>();

        dialog.getPersons().forEach(p -> persons.add(new PersonInDialog(p)));
    }
}

