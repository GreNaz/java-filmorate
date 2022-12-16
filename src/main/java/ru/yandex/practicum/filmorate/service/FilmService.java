package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.Collections;
import java.util.List;

/**
 * Responsible for operations with films, - adding and removing likes,
 * display the N most popular movies by the number of likes.
 * Each user can like the movie only once.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;

    public Film createLike(Long filmId, Long userId) {
        log.info("Start operation with like from " + userStorage.get(userId).get().getLogin() +
                " to film " + filmStorage.get(filmId).get().getName());
        likeStorage.createLike(filmId, userId);
        return get(filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        likeStorage.removeLike(filmId, userId);
        return get(filmId);
    }

    public Film create(Film film) {
        mpaStorage.injectMpa(film);
        Film newFilm = filmStorage.create(film);
        genreStorage.loadGenres(Collections.singletonList(newFilm));
        return newFilm;
    }

    public Film update(Film film) {
        mpaStorage.injectMpa(film);
        Film newFilm = filmStorage.update(film);
        genreStorage.loadGenres(Collections.singletonList(newFilm));
        return newFilm;
    }

    public List<Film> getFilms() {
        List<Film> films = filmStorage.getFilms();
        genreStorage.loadGenres(films);
        return films;
    }

    public Film get(Long id) {
        Film film = filmStorage.get(id).orElseThrow(
                () -> new FilmAlreadyExistException("Film id = " + id + " was not found"));
        genreStorage.loadGenres(Collections.singletonList(film));
        return film;
    }

    public List<Film> getPopular(int count) {
        List<Film> films = filmStorage.getPopular(count);
        genreStorage.loadGenres(films);
        return films;
    }
}
