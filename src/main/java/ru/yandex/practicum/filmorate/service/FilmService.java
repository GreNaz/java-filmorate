package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.model.dictionary.EventOperation;
import ru.yandex.practicum.filmorate.model.dictionary.FilmSearchBy;
import ru.yandex.practicum.filmorate.model.dictionary.FilmSortBy;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.marks.MarksStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.*;
import java.util.stream.Collectors;

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
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final LikeStorage likeStorage;
    private final DirectorStorage directorStorage;
    private final EventStorage eventStorage;
    private final MarksStorage marksStorage;


    public List<Film> getFilmsByDirectorWithSort(int id, FilmSortBy sortType) {
        log.info("Getting films By Director");
        List<Film> films = filmStorage.getByDirector(id, sortType);
        genreStorage.load(films);
        directorStorage.load(films);
        return films;
    }

    public List<Film> getByDirector(String query, Set<FilmSearchBy> by) {
        List<Film> films = new ArrayList<>();

        if (by.containsAll(List.of(FilmSearchBy.values()))) {
            log.info("Getting films by search in Directors and Titles");
            films = getByDirectorAndTitle(query);
        } else if (by.contains(FilmSearchBy.director)) {
            log.info("Getting films by search in Directors");
            films = getByDirector(query);
        } else if (by.contains(FilmSearchBy.title)) {
            log.info("Getting films by search in Titles");
            films = getByTitle(query);
        }
        return films;
    }

    private List<Film> getByTitle(String query) {
        log.info("Getting films by search in Titles");
        List<Film> films = filmStorage.getByTitle(query);
        genreStorage.load(films);
        directorStorage.load(films);
        return films;
    }

    private List<Film> getByDirector(String query) {
        log.info("Getting films by search in Directors");
        List<Film> films = directorStorage.getByDirector(query);
        genreStorage.load(films);
        directorStorage.load(films);
        return films;
    }

    private List<Film> getByDirectorAndTitle(String query) {
        log.info("Getting films by search in Directors and title");
        List<Film> films = filmStorage.getByDirectorAndTitle(query);
        genreStorage.load(films);
        directorStorage.load(films);
        return films;
    }

    public Film createLike(Long filmId, Long userId) {
        log.info("Adding a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
        likeStorage.create(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.ADD, filmId);
        eventStorage.create(event);
        log.info("Added the 'Like' event.");
        updateFilmRate(filmId);
        return get(filmId);
    }

    private void updateFilmRate(Long filmId) {
        Film film = filmStorage.get(filmId).orElseThrow(() ->
                new ObjectNotFoundException("Updated error, film not found"));
        film.setRate(likeStorage.getCount(filmId));
        log.info("The rating of the film {} has been updated", filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        log.info("Removing a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
        likeStorage.delete(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
        eventStorage.create(event);
        log.info("Added the 'Delete Like' event.");
        updateFilmRate(filmId);
        return get(filmId);
    }

    public Film create(Film film) {
        log.info("Making a film {}", film);
        mpaStorage.load(film);
        Film newFilm = filmStorage.create(film);
        genreStorage.load(Collections.singletonList(newFilm));
        return newFilm;
    }

    public Film update(Film film) {
        log.info("Updating a film {}", film);
        mpaStorage.load(film);
        Film newFilm = filmStorage.update(film);
        genreStorage.load(Collections.singletonList(newFilm));
        return newFilm;
    }

    public List<Film> getFilms() {
        log.info("Getting films");
        List<Film> films = filmStorage.get();
        genreStorage.load(films);
        directorStorage.load(films);
        return films;
    }

    public Film get(Long id) {
        log.info("Getting film id = {}", id);
        Film film = filmStorage.get(id).orElseThrow(
                () -> new AlreadyExistException("Film id = " + id + " was not found"));
        genreStorage.load(Collections.singletonList(film));
        directorStorage.load(Collections.singletonList(film));
        return film;
    }

    public List<Film> getPopular(int count) {
        log.info("Getting {} popular film(s)", count);
        List<Film> films = filmStorage.getPopular(count);
        genreStorage.load(films);
        return films;
    }

    public void delete(Long id) {
        filmStorage.delete(id);
        log.info("Film with id {} was deleted", id);
    }

    public List<Film> commonFilms(Long userId, Long friendId) {

        log.info("List of common films");
        List<Film> films = filmStorage.getCommon(userId, friendId);
        genreStorage.load(films);
        return filmStorage.getCommon(userId, friendId);
    }

    public List<Film> getPopularFilmByYear(int year) {
        log.info("Received popular films in {}", year);
        List<Film> films = filmStorage.getPopularByYear(year);
        genreStorage.load(films);
        return films;
    }

    public List<Film> getPopularFilmByGenre(int genreId) {
        log.info("Received popular film by genre {}", genreId);
        List<Film> films = filmStorage.getPopularByGenre(genreId);
        genreStorage.load(films);
        return films;
    }

    public List<Film> getPopularFilmByYearAndGenre(int year, int genreId) {
        log.info("Received a popular film in year {} and genre {}", year, genreId);
        List<Film> filmByYearAndGenre = filmStorage.getPopularByYearAndGenre(year, genreId);
        genreStorage.load(filmByYearAndGenre);
        return filmByYearAndGenre;
    }

    public void addMarks(Long filmId, Long userId, Integer mark) {
        boolean isPositive = true;
        if(mark <= 4) {
            isPositive = false;
        }
        marksStorage.addMarks(filmId, userId, isPositive);
    }

    public void deleteMarks(Long userId) {
        marksStorage.deleteMarks(userId);
    }

    public Double getMarksByFilm(Long filmId) {
        return marksStorage.getMarks(filmId);
    }
}
