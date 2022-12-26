package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.FilmSortBy;

import java.util.List;
import java.util.Optional;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    List<Film> get();

    List<Film> getByDirectorAndTitle(String query);

    List<Film> getByTitle(String query);

    List<Film> getByDirector(int id, FilmSortBy sortBy);

    Optional<Film> get(Long id);

    List<Film> get(List<Long> id);

    List<Film> getPopular(int count);

    void delete(Long id);

    List<Film> getCommon(Long userId, Long friendId);

    List<Film> getPopularByYear(int year);

    List<Film> getPopularByGenre(int genreId);

    List<Film> getPopularByYearAndGenre(int year, int genreId);

    List<Long> getIdOfCommon(List<Long> usersId, Long userId, int count);

}
