package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film); //  добавление нового фильма

    Film update(Film film); // модификация существующего фильма

    List<Film> getFilms(); // запрос списка всех фильмов

    Optional<Film> get(Long id); // запрос фильма по id

    List<Film> getPopular(int count);

    Film removeLike(Long filmId, Long userId);

    Film createLike(Long filmId, Long userId);
}
