package ru.yandex.practicum.filmorate.storage.event.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.dictionary.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> get(long id) {
        List<Event> events = jdbcTemplate.query("SELECT * FROM events WHERE user_id = ?", this::mapRowToEvent, id);
        log.info("A list of user id = " + id + " events has been sent");
        return events;
    }

    @Override
    public void create(Event event) {

        String sqlQuery = "INSERT INTO events (user_id,event_type, event_operation,entity_id,time_stamp)" +
                "VALUES (?,?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sqlQuery, new String[]{"event_id"});
            stmt.setLong(1, event.getUserId());
            stmt.setString(2, event.getEventType().getTitle());
            stmt.setString(3, event.getOperation().getTitle());
            stmt.setLong(4, event.getEntityId());
            stmt.setLong(5, event.getTimestamp());
            return stmt;
        }, keyHolder);
    }

    private Event mapRowToEvent(ResultSet rs, int rowNum) throws SQLException {
        return new Event(
                rs.getLong("event_id"),
                rs.getLong("user_id"),
                EventType.valueOf(rs.getString("event_type")),
                EventOperation.valueOf(rs.getString("event_operation")),
                rs.getLong("entity_id"),
                rs.getLong("time_stamp")
        );
    }
}
