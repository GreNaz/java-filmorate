package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.likes.LikeStorage;
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

    public List<Film> getFilmsByDirectorWithSort(int id, String sortType) {
        log.info("Getting films By Director");
        List<Film> films = filmStorage.getFilms();
        genreStorage.loadGenres(films);
        directorStorage.loadDirectors(films);
        Director director = directorStorage.get(id).orElseThrow();
        if (sortType.equals("year")){
            return getFilmsByDirector(films, director).stream()
                    .sorted(Comparator.comparing(Film::getReleaseDate, Comparator.nullsLast(Comparator.naturalOrder())))
                    .collect(Collectors.toList());
        } else if (sortType.equals("likes")){
            List<Film> filmsByDirector = getFilmsByDirector(films, director);
                return sortFilmsByLikes(filmsByDirector);
        } else {
            return getFilmsByDirector(films, director);
        }
    }

    public Film createLike(Long filmId, Long userId) {
        log.info("Adding a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
        likeStorage.createLike(filmId, userId);
        return get(filmId);
    }

    public Film removeLike(Long filmId, Long userId) {
        log.info("Removing a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
        likeStorage.removeLike(filmId, userId);
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
                () -> new FilmAlreadyExistException("Film id = " + id + " was not found"));
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

    private List<Film> getFilmsByDirector(List<Film> films, Director director){
        return films.stream()
                .filter(f -> f.getDirectors().contains(director))
                .collect(Collectors.toList());
    }

    private List<Film> sortFilmsByLikes (List<Film> films) {
        Map<Long, Integer> filmsAndLikes = new HashMap<>();
        for (Film film : films) {
            filmsAndLikes.put(film.getId(), likeStorage.getLikesNumber(film.getId()));
        }
        if (filmsAndLikes.size() < 2) {
            return films;
        } else {
            Map<Long, Integer> filmsAndLikesSorted = filmsAndLikes.entrySet()
                    .stream()
                    .sorted(Map.Entry.comparingByValue())
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (oldValue, newValue) -> oldValue, LinkedHashMap::new));
            List<Film> filmsByDirectorSortedByLikes = new ArrayList<>();
            for (Long filmId : filmsAndLikesSorted.keySet()) {
                filmsByDirectorSortedByLikes.add(filmStorage.get(filmId).get());
            }
            genreStorage.loadGenres(filmsByDirectorSortedByLikes);
            directorStorage.loadDirectors(filmsByDirectorSortedByLikes);
            return filmsByDirectorSortedByLikes;
        }
    }
}
