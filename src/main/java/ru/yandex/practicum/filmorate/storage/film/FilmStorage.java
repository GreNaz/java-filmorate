package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Optional<List<Film>> searchFilmsByTitle(String query);

    Optional<Film> get(Long id);

    List<Film> getPopular(int count);

    void deleteById(Long id);

    List<Film> commonFilms(Long userId, Long friendId);

}
