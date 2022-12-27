package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.storage.MarksStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarksStorage marksStorage;

    //Добавление новой оценки или обновление существующей
    public void create(Long filmId, Long userId, int mark) {
        marksStorage.create(filmId, userId, mark);
    }

    public void delete(Long filmId, Long userId) {
        marksStorage.delete(filmId, userId);
    }

    public String getByFilm(Long filmId) {
        return String.format("%.1f", marksStorage.getByFilm(filmId));
    }
}
