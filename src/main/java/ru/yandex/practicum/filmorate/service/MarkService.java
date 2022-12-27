package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MarksStorage;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarksStorage marksStorage;
    private final FilmStorage filmStorage;

    //Добавление новой оценки или обновление существующей
    public Film create(Long filmId, Long userId, int mark) {
        marksStorage.create(filmId, userId, mark);
        Film film = filmStorage.get(filmId).get();
        updateFilmRate(filmId);
        return film;
    }

    private void updateFilmRate(Long filmId) {
        Film film = filmStorage.get(filmId).orElseThrow(() ->
                new ObjectNotFoundException("Updated error, film not found"));
        film.setRate(marksStorage.getRate(filmId));
        log.info("The rating of the film {} has been updated", filmId);
    }

    public void delete(Long filmId, Long userId) {
        marksStorage.delete(filmId, userId);
    }

    public String getByFilm(Long filmId) {
        return String.format("%.1f", marksStorage.getRate(filmId));
    }
}
