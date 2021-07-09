package main.model;

import lombok.Getter;

@Getter
public enum LikeType {
    POST("POST"),
    COMMENT("COMMENT");
    private final String name;

    LikeType(String name){
        this.name = name;
    }
}
