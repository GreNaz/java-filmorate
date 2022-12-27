package ru.yandex.practicum.filmorate.storage.marks.implMarks;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mark;
import ru.yandex.practicum.filmorate.storage.marks.MarksStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarksDao implements MarksStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void addMarks(Long filmId, Long userId, boolean is_positive) {
        final String addMarks = "INSERT INTO MARKS (FILM_ID, USER_ID, IS_POSITIVE)" +
                "VALUES (?, ?, ?)";
        jdbcTemplate.update(addMarks, filmId, userId, is_positive);
    }

    @Override
    public void deleteMarks(Long user_id) {
        final String deleteMarks = "DELETE FROM MARKS WHERE MARKS.USER_ID = ?";
        jdbcTemplate.update(deleteMarks);
    }

    @Override
    public Double getMarks(Long filmId) {
        final String getMarks = "SELECT COUNT(IS_POSITIVE) " +
                "FROM MARKS " +
                "LEFT JOIN FILMS f ON MARKS.FILM_ID = f.FILM_ID " +
                "WHERE f.FILM_ID = ?";
        Integer countFotFilm = jdbcTemplate.queryForObject(getMarks, Integer.class, filmId);
        final String getPositiveMarks = "SELECT COUNT(IS_POSITIVE) " +
                "FROM MARKS " +
                "LEFT JOIN FILMS f ON MARKS.FILM_ID = f.FILM_ID " +
                "WHERE f.FILM_ID = ? AND IS_POSITIVE = true";
        Integer positiveMarksForFilm =
                jdbcTemplate.queryForObject(getPositiveMarks, Integer.class, filmId);
        return (double) countFotFilm/positiveMarksForFilm;
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

