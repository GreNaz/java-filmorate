package ru.yandex.practicum.filmorate.model;

public enum EventOperation {

    REMOVE("REMOVE"),
    ADD("ADD"),
    UPDATE("UPDATE");

    private final String title;

    EventOperation(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
