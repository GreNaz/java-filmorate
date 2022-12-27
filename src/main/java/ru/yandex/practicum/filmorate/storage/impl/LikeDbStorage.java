package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.storage.LikeStorage;

import javax.validation.constraints.Positive;

@Repository
@RequiredArgsConstructor

public class LikeDbStorage implements LikeStorage {

//    private final JdbcTemplate jdbcTemplate;
//
//    @Override
//    public void delete(Long filmId, Long userId) {
//        String sql = "DELETE FROM films_likes " +
//                "WHERE film_id = ? AND user_id = ?";
//
//        int resultUpdate = jdbcTemplate.update(sql, filmId, userId);
//
//        if (resultUpdate == 0) {
//            throw new ObjectNotFoundException("Not found film or user");
//        }
//    }
//
//    @Override
//    public void create(Long filmId, Long userId) {
//        String sql = "MERGE INTO FILMS_LIKES (FILM_ID, USER_ID) VALUES (?, ?)";
//
//        int resultUpdate = jdbcTemplate.update(sql, filmId, userId);
//        if (resultUpdate == 0) {
//            throw new ObjectNotFoundException("Not found film or user");
//        }
//    }
//
//    @Override
//    public Double getRate(Long filmId) {
//        String sql = "SELECT COUNT(*) FROM films_likes WHERE film_id = ?";
//        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, filmId);
//        if (!filmRows.next()) {
//            return 0.0;
//        } else {
//            return jdbcTemplate.queryForObject(sql, Double.class, filmId);
//        }
//    }
}
