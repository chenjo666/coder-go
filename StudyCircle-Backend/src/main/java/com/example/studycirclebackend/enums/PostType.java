package com.example.studycirclebackend.enums;

public enum PostType {
    ALL("all"),
    DISCUSSION("discussion"),
    HELP("help"),
    SHARING("sharing"),
    TUTORIAL("tutorial"),
    EMOTIONAL("emotional"),
    NEWS("news"),
    REVIEW("review"),
    SURVEY("survey"),
    OTHER("other");

    private String value;
    PostType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
