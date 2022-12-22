package ru.yandex.practicum.filmorate.storage.likes;

import java.util.Optional;

public interface LikeStorage {

    void removeLike(Long filmId, Long userId);

    void createLike(Long filmId, Long userId);

    Integer getLikesNumber(Long filmId);
}
