package ru.yandex.practicum.filmorate.storage.marks.implMarks;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mark;
import ru.yandex.practicum.filmorate.storage.marks.MarksStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarksDbStorage implements MarksStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addMarks(Long filmId, Long userId, Integer mark ) {
        final String addMarks = "INSERT INTO MARKS (FILM_ID, USER_ID, MARK)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(addMarks, filmId, userId, mark);
    }

    @Override
    public void deleteMarks(Long filmId, Long userId) {
        final String deleteMarks = "DELETE FROM MARKS WHERE MARKS.Film_id = ? AND MARKS.USER_ID = ?";
        jdbcTemplate.update(deleteMarks, filmId, userId);
    }

    @Override
    public Double getMarks(Long filmId) {
        final String getMarks = "SELECT AVG(MARK) " +
                "FROM MARKS m " +
                "LEFT JOIN FILMS f ON f.film_id = m.film_id " +
                "WHERE f.film_id = ? ";
        return jdbcTemplate.queryForObject(getMarks,Double.class,filmId);
    }

    @Override
    public List<Mark> getMarksByUser(Long userId) {
        final String getMarkByUser = "SELECT *\n" +
                "FROM MARKS\n" +
                "LEFT JOIN USERS U on MARKS.USER_ID = U.USER_ID\n" +
                "WHERE u.USER_ID = 2\n" +
                "ORDER BY (IS_POSITIVE = true)";
        return List.of();
    }
}

