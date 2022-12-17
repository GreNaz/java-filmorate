package ru.yandex.practicum.filmorate.storage.likes.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;

import java.util.NoSuchElementException;

@Repository
@RequiredArgsConstructor
public class LikeDbStorage implements LikeStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public void removeLike(Long filmId, Long userId) {
        String sql = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";

        int resultUpdate = jdbcTemplate.update(sql, filmId, userId);

        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("NOT FOUND FILM OR USER");
        }
    }

    @Override
    public void createLike(Long filmId, Long userId) {
        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";

        int resultUpdate = jdbcTemplate.update(sql, filmId, userId);
        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("NOT FOUND FILM OR USER");
        }

    }
}
