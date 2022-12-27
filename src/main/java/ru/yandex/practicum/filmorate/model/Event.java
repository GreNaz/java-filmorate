package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.dictionary.EventOperation;

import java.time.Instant;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class Event {
    private long eventId;
    private long userId;
    private EventType eventType;
    private EventOperation operation;
    private long entityId;
    private long timestamp;

    public Event(long userId, EventType eventType, EventOperation eventOperation, long entityId) {
        this.userId = userId;
        this.eventType = eventType;
        this.operation = eventOperation;
        this.entityId = entityId;
        this.timestamp = Instant.now().toEpochMilli();
    }

}
