package ru.yandex.practicum.filmorate.model;

public enum EventType {
    LIKE("LIKE"),
    FRIEND("FRIEND"),
    REVIEW("REVIEW"),
    ;
    private final String title;

    EventType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
