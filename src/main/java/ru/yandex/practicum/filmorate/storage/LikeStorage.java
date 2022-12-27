package ru.yandex.practicum.filmorate.storage;

import javax.validation.constraints.Positive;

public interface LikeStorage {

    void delete(Long filmId, Long userId);

    void create(Long filmId, Long userId);

    @Positive Double getCount(Long filmId);
}
