package ru.yandex.practicum.filmorate.storage.likes;

public interface LikeStorage {

    void delete(Long filmId, Long userId);

    void create(Long filmId, Long userId);

    Integer getCount(Long filmId);
}
