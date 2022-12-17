package ru.yandex.practicum.filmorate.storage.likes;

public interface LikeStorage {

    void removeLike(Long filmId, Long userId);

    void createLike(Long filmId, Long userId);
}
