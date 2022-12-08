package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.FilmAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryFilmStorage implements FilmStorage {
    private long id;

    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Film create(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Add new film: {}", film);
        return film;
    }

    @Override
    public Film update(Film film) {
        if (films.values().stream().anyMatch(film1 -> film1.getId() == film.getId())) {
            films.put(film.getId(), film);
            log.info("Film {}, update: {}", film.getName(), film);
            return film;
        } else {
            log.error("Film with id : " + film.getId() + " was not found");
            throw new FilmAlreadyExistException("No such film exists with transmitted id");
        }
    }

    @Override
    public List<Film> getFilms() {
        return List.copyOf(films.values());
    }

    @Override
    public Optional<Film> get(Long id) {
        return Optional.ofNullable(films.get(id));
    }

    @Override
    public List<Film> getPopular(int count) {

        return films.values().stream()
                .sorted((o1, o2) -> Integer.compare(o2.getLikes().size(), o1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {
        films.get(filmId).getLikes().remove(userId);
        return films.get(filmId);
    }

    @Override
    public Film createLike(Long filmId, Long userId) {
        films.get(filmId).addLike(userId);
        return films.get(filmId);
    }
}
