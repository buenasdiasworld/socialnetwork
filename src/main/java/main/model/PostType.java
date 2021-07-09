package main.model;

import lombok.Getter;

@Getter
public enum PostType {
    POSTED("POSTED"),
    QUEUED("QUEUED");
    private final String name;

    PostType(String name){
        this.name = name;
    }

}
