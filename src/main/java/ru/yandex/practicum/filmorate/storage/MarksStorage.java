package ru.yandex.practicum.filmorate.storage;

import java.util.Optional;

public interface MarksStorage {

    void create(Long filmId, Long userId, int isPositive);

    void delete(Long filmId, Long userId);

    Optional<Double> getRate(Long filmId);

//    List<Mark> getByUser(Long userId);
}
