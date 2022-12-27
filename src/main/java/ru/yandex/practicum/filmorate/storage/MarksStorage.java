package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Mark;

import java.util.List;

public interface MarksStorage {

    void create(Long filmId, Long userId, int isPositive);

    void delete(Long filmId, Long userId);

    double getRate(Long filmId);

    List<Mark> getByUser(Long userId);
}
