package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.friends.FriendStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import javax.validation.ValidationException;
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
    private final FriendStorage friendStorage;

    public List<Film> getFilmsByDirectorWithSort(int id, String sortType) {
        log.info("Getting films By Director");
        List<Film> films = filmStorage.getFilms();
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);
        Director director = directorStorage.get(id).orElseThrow();
        if (sortType.equals("year")) {
            return getFilmsByDirector(films, director).stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        } else if (sortType.equals("likes")) {
            return getFilmsByDirector(films, director).stream()
                    .sorted(Comparator.comparing(Film::getRate, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        } else {
            return getFilmsByDirector(films, director);
        }
    }

    private List<Film> getFilmsByDirector(List<Film> films, Director director) {
        return films.stream()
                .filter(f -> f.getDirectors().contains(director))
                .collect(Collectors.toList());
    }
    private final EventStorage eventStorage;

    public List<Film> searchFilmsByDirectorAndTitle(String query, String by) {
        List<Film> films = new ArrayList<>();
        switch (by){
            case "director":
                log.info("Getting films by search in Directors");
                films = searchFilmsByDirector(query);
                break;
            case "title":
                log.info("Getting films by search in Titles");
                films = searchFilmsByTitle(query);
                break;
            case "title,director":
                log.info("Getting films by search in Directors and Titles");
                films = searchFilmsByDirector(query);
                films.addAll(searchFilmsByTitle(query));
                break;
            default:

        }
        return  films;
    }

    private List<Film> searchFilmsByTitle(String query) {
        log.info("Getting films by search in Titles");
        List<Film> films = filmStorage.searchFilmsByTitle(query).orElseThrow(() -> new AlreadyExistException("Film by query = " + query + " was not found"));
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);
        return films;
    }

    private List<Film> searchFilmsByDirector(String query) {
        log.info("Getting films by search in Directors");
        List<Film> films = new ArrayList<>();
        List<Film> allFilms = filmStorage.getFilms();
        genreStorage.loadGenres(allFilms);
        directorStorage.loadDirectors(allFilms);
        List<Director> directors = directorStorage.searchDirectors(query).orElseThrow(() -> new AlreadyExistException("Film by query = " + query + " was not found"));
        for (Director director: directors) {
            films.addAll(getFilmsByDirector(allFilms, director));
        }
        return films;
    }

    public Film createLike(Long filmId, Long userId) {
        log.info("Adding a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
        likeStorage.createLike(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.ADD, filmId);
        eventStorage.addEvent(event);
        log.info("Added the 'Like' event.");
        updateFilmRate(filmId);
        return get(filmId);
    }

    private void updateFilmRate(Long filmId) {
        Film updatedFilm = filmStorage.get(filmId).orElseThrow();
        genreStorage.loadGenres(Collections.singletonList(updatedFilm));
        directorStorage.loadDirectors(Collections.singletonList(updatedFilm));
        updatedFilm.setRate(likeStorage.getLikesNumber(filmId));
        update(updatedFilm);
        System.out.println(updatedFilm);
    }

    public Film removeLike(Long filmId, Long userId) {
        log.info("Removing a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
        likeStorage.removeLike(filmId, userId);
        Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
        eventStorage.addEvent(event);
        log.info("Added the 'Delete Like' event.");
        updateFilmRate(filmId);
        return get(filmId);
    }

    public Film create(Film film) {
        log.info("Making a film {}", film);
        mpaStorage.injectMpa(film);
        Film newFilm = filmStorage.create(film);
        genreStorage.loadGenres(Collections.singletonList(newFilm));
        return newFilm;
    }

    public Film update(Film film) {
        log.info("Updating a film {}", film);
        mpaStorage.injectMpa(film);
        Film newFilm = filmStorage.update(film);
        genreStorage.loadGenres(Collections.singletonList(newFilm));
        return newFilm;
    }

    public List<Film> getFilms() {
        log.info("Getting films");
        List<Film> films = filmStorage.getFilms();
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);
        return films;
    }

    public Film get(Long id) {
        log.info("Getting film id = {}", id);
        Film film = filmStorage.get(id).orElseThrow(
                () -> new AlreadyExistException("Film id = " + id + " was not found"));
        genreStorage.loadGenres(Collections.singletonList(film));
        directorStorage.loadDirectors(Collections.singletonList(film));
        return film;
    }

    public List<Film> getPopular(int count) {
        log.info("Getting {} popular film(s)", count);
        List<Film> films = filmStorage.getPopular(count);
        genreStorage.loadGenres(films);
        return films;
    }

    public void deleteById(Long id) {
        filmStorage.deleteById(id);
        log.info("Film with id {} was deleted", id);
    }

    public List<Film> commonFilms(Long userId, Long friendId) {

        log.info("List of common films");
        List<Film> films = filmStorage.commonFilms(userId, friendId);
        genreStorage.loadGenres(films);
        return filmStorage.commonFilms(userId, friendId);
    }

    public List<Film> getPopularFilmByYear(int year) {
        log.info("Received popular films in {}", year);
        List<Film> films = filmStorage.getPopularFilmByYear(year);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> getPopularFilmByGenre(int genreId) {
        log.info("Received popular film by genre {}", genreId);
        List<Film> films = filmStorage.getPopularFilmByGenre(genreId);
        genreStorage.loadGenres(films);
        return films;
    }

    public List<Film> getPopularFilmByYearAndGenre(int year, int genreId) {
        log.info("Received a popular film in year {} and genre {}", year, genreId);
        List<Film> filmByYearAndGenre = filmStorage.getPopularFilmByYearAndGenre(year, genreId);
        genreStorage.loadGenres(filmByYearAndGenre);
        return filmByYearAndGenre;
    }
}
