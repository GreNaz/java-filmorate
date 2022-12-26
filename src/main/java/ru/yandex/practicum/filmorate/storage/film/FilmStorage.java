package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> get();

    Optional<List<Film>> searchByTitle(String query);

    Optional<Film> get(Long id);

    Optional<List<Film>> get(List<Long> id);

    List<Film> getPopular(int count);

    void delete(Long id);

    List<Film> getCommon(Long userId, Long friendId);

    List<Film> getPopularByYear(int year);

    List<Film> getPopularByGenre(int genreId);

    List<Film> getPopularByYearAndGenre(int year, int genreId);

    List<Long> getIdOfCommon(List<Long> usersId, Long userId, int count);

    List<Film> getFilmByDirector(int id);

}
