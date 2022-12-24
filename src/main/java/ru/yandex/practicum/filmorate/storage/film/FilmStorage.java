package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> getFilms();

    Optional<List<Film>> searchFilmsByTitle(String query);

    Optional<Film> get(Long id);

    Optional<List<Film>> get(List<Long> id);

    List<Film> getPopular(int count);

    void deleteById(Long id);

    List<Film> commonFilms(Long userId, Long friendId);

    List<Long> idCommonFilms(List<Long> usersId, Long userId, int count);
}
