package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.MarksStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarksDbStorage implements MarksStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Long filmId, Long userId, int mark) {
        final String sql = "MERGE INTO FILM_MARKS (FILM_ID, USER_ID, MARK) VALUES (?, ?, ?)";
        final String setRate = "UPDATE FILMS SET RATE = ? WHERE FILM_ID = ?";
        int resultUpdate = jdbcTemplate.update(sql, filmId, userId, mark);
        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Not found film or user");
        }
        resultUpdate = jdbcTemplate.update(setRate, getRate(filmId), filmId);
        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Error in process calculate film rate");
        }
    }

    @Override
    public void delete(Long filmId, Long userId) {
        final String delete = "DELETE FROM FILM_MARKS WHERE FILM_MARKS.Film_id = ? AND FILM_MARKS.USER_ID = ?";
        int resultUpdate = jdbcTemplate.update(delete, filmId, userId);
        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Error in process deleting film mark");
        }
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
                "         LEFT JOIN FILM_MARKS FM ON FM.film_id = films.film_id\n" +
                "LEFT JOIN MPA M on M.MPA_ID = FILMS.MPA_ID\n" +
                "WHERE FM.USER_ID = ?\n" +
                "ORDER BY FM.MARK DESC";
        return jdbcTemplate.query(getMarksByUser, Mapper::filmMapper, userId);
    }

    @Override
    public Double getMarkByUser(Long userId, Long filmId) {
        final String getMarksByUser = "SELECT MARK\n" +
                "FROM FILM_MARKS\n" +
                "WHERE USER_ID = ?\n" +
                "  AND FILM_ID = ?";
        return jdbcTemplate.queryForObject(getMarksByUser, Double.class, userId, filmId);
    }

}

