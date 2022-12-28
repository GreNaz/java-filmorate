package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.MarksStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MarksDbStorage implements MarksStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Long filmId, Long userId, int mark) {
        //мерждим чтобы была возможность изменить оценку
        String sql = "MERGE INTO FILM_MARKS (FILM_ID, USER_ID, MARK) VALUES (?, ?, ?)";
        final String setRate = "UPDATE FILMS SET RATE = ? WHERE FILM_ID = ?";
        jdbcTemplate.update(setRate, getRate(filmId), filmId);
        int resultUpdate = jdbcTemplate.update(sql, filmId, userId, mark);
        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Not found film or user");
        }
    }

    @Override
    public void delete(Long filmId, Long userId) {
        final String delete = "DELETE FROM FILM_MARKS WHERE FILM_MARKS.Film_id = ? AND FILM_MARKS.USER_ID = ?";
        jdbcTemplate.update(delete, filmId, userId);
    }

    @Override
    public Double getRate(Long filmId) {
        final String getMarks = "SELECT AVG(MARK) " +
                "FROM FILM_MARKS m " +
                "LEFT JOIN FILMS f ON f.film_id = m.film_id " +
                "WHERE f.film_id = ? ";

        return jdbcTemplate.queryForObject(getMarks, Double.class, filmId);
    }

    @Override
    public List<Film> getMarksByUser(Long userId) {
        final String getMarksByUser = "SELECT *\n" +
                "FROM FILMS\n" +
                "LEFT JOIN FILM_MARKS FM ON FM.film_id = films.film_id\n" +
                "WHERE FM.USER_ID = ?\n" +
                "ORDER BY FM.MARK DESC";
        return jdbcTemplate.query(getMarksByUser, Mapper::filmMapper, userId);
    }
}

