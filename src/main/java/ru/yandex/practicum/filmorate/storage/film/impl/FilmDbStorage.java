package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.util.mapper.Mapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Film create(Film film) {

        String sql = "INSERT INTO films (name, description, release_date, duration, mpa_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"film_id"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);

        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        addGenres(film);

        addDirectors(film);

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE FILM_ID = ?";

        deleteGenres(film);
        addGenres(film);

        deleteDirectors(film);
        addDirectors(film);

        int resultUpdate = jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        if (resultUpdate == 0) {
            throw new AlreadyExistException("NOT FOUND FILM: " + film);
        }
        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id";
        return jdbcTemplate.query(sql, Mapper::filmMapper);
    }

    @Override
    public Optional<Film> get(Long id) {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id " +
                "WHERE films.film_id = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!filmRows.next()) {
            return Optional.empty();
        }

        try {
            Film film = jdbcTemplate.queryForObject(sql, Mapper::filmMapper, id);
            return Optional.ofNullable(film);
        } catch (DataAccessException dataAccessException) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> getPopular(int count) {

        String sql = "SELECT films.FILM_ID, films.name, description, release_date, duration, rate, m.mpa_id, m.name " +
                "FROM films " +
                "LEFT JOIN films_likes fl ON films.FILM_ID = fl.film_id " +
                "LEFT JOIN mpa m on m.MPA_ID = films.mpa_id " +
                "GROUP BY films.FILM_ID, fl.film_id IN ( " +
                "SELECT film_id " +
                "FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, Mapper::filmMapper, count);
    }

    private void addGenres(Film film) {
        if (film.getGenres() != null) {
            String updateGenres = "MERGE INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    updateGenres, film.getGenres(), film.getGenres().size(),
                    (ps, genre) -> {
                        ps.setLong(1, film.getId());
                        ps.setInt(2, genre.getId());
                    });
            film.getGenres().clear();
        } else film.setGenres(new LinkedHashSet<>());
    }

    private void deleteGenres(Film film) {
        String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
        jdbcTemplate.update(deleteGenres, film.getId());
    }

    private void addDirectors(Film film) {
        if (film.getDirectors() != null) {
            String updateDirectors = "MERGE INTO film_director (film_id, director_id) VALUES (?, ?)";
            jdbcTemplate.batchUpdate(
                    updateDirectors, film.getDirectors(), film.getDirectors().size(),
                    (ps, director) -> {
                        ps.setLong(1, film.getId());
                        ps.setInt(2, director.getId());
                    });
        } else film.setDirectors(new LinkedHashSet<>());
    }

    private void deleteDirectors(Film film) {
        String deleteDirectors = "DELETE FROM film_director WHERE film_id = ?";
        jdbcTemplate.update(deleteDirectors, film.getId());
    }

}