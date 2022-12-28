package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface MarksStorage {

    void create(Long filmId, Long userId, int isPositive);

    void delete(Long filmId, Long userId);

    Double getRate(Long filmId);

    List<Film> getMarksByUser(Long userId);

    Double getMarkByUser(Long userId, Long filmId);
}
