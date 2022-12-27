package ru.yandex.practicum.filmorate.storage.marks;

import ru.yandex.practicum.filmorate.model.Mark;

import java.util.List;

public interface MarksStorage {

    void addMarks(Long filmId, Long userId, Integer isPositive);

    void deleteMarks(Long filmId, Long userId);

    Double getMarks(Long filmId);

    List<Mark> getMarksByUser(Long userId);
}
