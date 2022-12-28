package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Mark;
import ru.yandex.practicum.filmorate.storage.MarksStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MarksDbStorage implements MarksStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void create(Long filmId, Long userId, int mark) {
        //мерждим чтобы была возможность изменить оценку
        String sql = "MERGE INTO FILM_MARKS (FILM_ID, USER_ID, MARK) VALUES (?, ?, ?)";
        int resultUpdate = jdbcTemplate.update(sql, filmId, userId, mark);
        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Not found film or user");
        }
    }

    @Override
    public void delete(Long filmId, Long userId) {
        final String delete = "DELETE FROM FILM_MARKS WHERE MARKS.Film_id = ? AND MARKS.USER_ID = ?";
        jdbcTemplate.update(delete, filmId, userId);
    }

    @Override
    public double getRate(Long filmId) {
        final String getMarks = "SELECT AVG(MARK)/COUNT(*) " +
                "FROM FILM_MARKS m " +
                "LEFT JOIN FILMS f ON f.film_id = m.film_id " +
                "WHERE f.film_id = ? ";

        return jdbcTemplate.queryForObject(getMarks, Double.class, filmId);
    }

//    @Override
//    public List<Mark> getByUser(Long userId) {
//        final String getMarkByUser = "SELECT *\n" +
//                "FROM MARKS\n" +
//                "LEFT JOIN USERS U on MARKS.USER_ID = U.USER_ID\n" +
//                "WHERE u.USER_ID = 2\n" +
//                "ORDER BY (IS_POSITIVE = true)";
//        return List.of();
//    }
}

