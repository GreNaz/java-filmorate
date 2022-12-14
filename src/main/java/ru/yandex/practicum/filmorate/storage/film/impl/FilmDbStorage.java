package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

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

        return film;
    }

    @Override
    public Film update(Film film) {
        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE FILM_ID = ?";

        deleteGenres(film);
        addGenres(film);
        jdbcTemplate.update(sql,
                film.getName(), film.getDescription(), film.getReleaseDate(),
                film.getDuration(), film.getMpa().getId(), film.getId());

        return film;
    }

    @Override
    public List<Film> getFilms() {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id";
        return jdbcTemplate.query(sql, this::makeFilm);
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
        Film film = jdbcTemplate.queryForObject(sql, this::makeFilm, id);
        return Optional.ofNullable(film);
    }

    @Override
    public List<Film> getPopular(int count) {

        String sql = "SELECT films.FILM_ID, films.name, description, release_date, duration, m.mpa_id, m.name " +
                "FROM films " +
                "LEFT JOIN films_likes fl ON films.FILM_ID = fl.film_id " +
                "LEFT JOIN mpa m on m.MPA_ID = films.mpa_id " +
                "GROUP BY films.FILM_ID, fl.film_id IN ( " +
                "SELECT film_id " +
                "FROM films_likes " +
                ") " +
                "ORDER BY COUNT(fl.film_id) DESC " +
                "LIMIT ?";

        return jdbcTemplate.query(sql, this::makeFilm, count);
    }

    private Film makeFilm(ResultSet resultSet, int rowNum) throws SQLException {

        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        Mpa mpa = new Mpa(resultSet.getInt("mpa.mpa_id"), resultSet.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new ArrayList<>());
    }

    private void addGenres(Film film) {
        if (film.getGenres() != null) {
            String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            film.setGenres(film.getGenres().stream()
                    .map(Genre::getId)
                    .distinct()
                    .map(id -> new Genre(id, null))
                    .collect(Collectors.toList()));
            jdbcTemplate.batchUpdate(updateGenres,
                    new BatchPreparedStatementSetter() {

                        public void setValues(PreparedStatement ps, int i) throws SQLException {
                            ps.setLong(1, film.getId());
                            ps.setInt(2, film.getGenres().get(i).getId());
                        }

                        public int getBatchSize() {
                            return film.getGenres().size();
                        }
                    });
            film.getGenres().clear();
        } else {
            film.setGenres(Collections.emptyList());
        }
    }

    private void deleteGenres(Film film) {
        if (film.getGenres() != null) {
            String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
            jdbcTemplate.update(deleteGenres, film.getId());
        }
    }

}