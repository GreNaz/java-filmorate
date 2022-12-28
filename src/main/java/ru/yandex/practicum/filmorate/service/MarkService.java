package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MarksStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MarkService {
    private final MarksStorage marksStorage;
    private final FilmStorage filmStorage;

    //Добавление новой оценки или обновление существующей
    public Film create(Long filmId, Long userId, int mark) {
        marksStorage.create(filmId, userId, mark);
        return updateFilmRate(filmId);
    }

    public void delete(Long filmId, Long userId) {
        marksStorage.delete(filmId, userId);
        updateFilmRate(filmId);
        log.info("The rating of the film {} has been deleted", filmId);
    }

    private Film updateFilmRate(Long filmId) {
        Film film = filmStorage.get(filmId).orElseThrow(() ->
                new ObjectNotFoundException("Updated error, film with id " + filmId + " not found"));
        film.setRate(marksStorage.getRate(filmId));
        log.info("The rating of the film {} has been updated", filmId);
        return film;

    }

    public Double getByFilm(Long filmId) {
        return marksStorage.getRate(filmId);
    }

    public List<Film> getByUser(Long userId) {
        log.info("A list of ratings that the user has set has been received");
        return marksStorage.getMarksByUser(userId);
    }
}
