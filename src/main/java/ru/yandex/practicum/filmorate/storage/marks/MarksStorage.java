package ru.yandex.practicum.filmorate.storage.marks;

import ru.yandex.practicum.filmorate.model.Mark;

import java.util.List;

public interface MarksStorage {

    void addMarks(Long filmId, Long userId, boolean isPositive);

    void deleteMarks(Long userId);

    Double getMarks(Long filmId);

    List<Mark> getMarksByUser(Long userId);
}
