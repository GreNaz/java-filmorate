package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.Optional;

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
    private final MpaStorage mpaStorage;

    public Film createLike(Long filmId, Long userId) {

        validation(userId, filmId);

        Optional<User> user = userStorage.get(userId);
        Optional<Film> film = filmStorage.get(filmId);

        log.info("Added like from user " + user.get().getEmail() +
                " to film " + film.get().getName());

        return filmStorage.createLike(filmId, userId);
    }

    public Film removeLike(Long filmId, Long userId) {

        validation(userId, filmId);

        Optional<User> user = userStorage.get(userId);
        Optional<Film> film = filmStorage.get(filmId);

        log.info("Removed like from user " + user.get().getEmail() +
                " to film " + film.get().getName());

        return filmStorage.removeLike(filmId, userId);

    }

    public Film create(Film film) {
        mpaStorage.injectMpa(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validation(film);
        mpaStorage.injectMpa(film);
        return filmStorage.update(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film get(Long id) {
        return filmStorage.get(id).orElseThrow(
                () -> new FilmAlreadyExistException("Film id = " + id + " was not found"));
    }

    public List<Film> getPopular(int count) {
        return filmStorage.getPopular(count);
    }

    private void validation(Long userId, Long filmId) {
        log.debug("Start of validation");
        get(filmId);
        if (userStorage.get(userId).isEmpty()) {
            log.error("An error has occurred. Invalid input user data");
            throw new UserAlreadyExistException("User id = " + userId + " was not found");
        }
        log.debug("Successful validation");
    }

    private void validation(Film film) {
        get(film.getId());
    }
}
