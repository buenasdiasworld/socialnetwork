package main.data.response.type;

import lombok.Data;
import main.model.Language;

@Data
public class LanguageList {
    private int id;
    private String title;

    public LanguageList(Language language) {
        id = language.getId();
        title = language.getTitle();
    }
}
