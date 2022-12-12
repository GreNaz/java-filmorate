package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mpa.impl.MpaDbStorage;

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
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final MpaDbStorage mpaDbStorage;

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
        film.setMpa(mpaDbStorage.get(film.getMpa().getId()).orElseThrow());
        String genresSql = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
        if (film.getGenres() != null) {
            film.getGenres().stream()
                    .map(genre -> jdbcTemplate.update(genresSql, film.getId(), genre.getId()))
                    .collect(Collectors.toList());
            film.getGenres().clear();
            loadGenres(Collections.singletonList(film));
        } else film.setGenres(Collections.emptyList());
        return film;
    }

    @Override
    public Film update(Film film) {

        String sql = "UPDATE films SET name = ?, description = ?, release_date = ?, " +
                "duration = ?, mpa_id = ?" +
                "WHERE FILM_ID = ?";
        film.setMpa(mpaDbStorage.get(film.getMpa().getId()).orElseThrow());
        if (film.getGenres() != null) {
            String deleteGenres = "DELETE FROM film_genre WHERE film_id = ?";
            String updateGenres = "INSERT INTO film_genre (film_id, genre_id) VALUES (?, ?)";
            jdbcTemplate.update(deleteGenres, film.getId());
            for (Genre g : film.getGenres()) {
                String checkDuplicate = "SELECT * FROM film_genre WHERE film_id = ? AND genre_id = ?";
                SqlRowSet checkRows = jdbcTemplate.queryForRowSet(checkDuplicate, film.getId(), g.getId());
                if (!checkRows.next()) {
                    jdbcTemplate.update(updateGenres, film.getId(), g.getId());
                }
            }
            film.getGenres().clear();
            loadGenres(Collections.singletonList(film));
        } else film.setGenres(Collections.emptyList());
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

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm);

        loadGenres(films);

        return films;
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

        loadGenres(Collections.singletonList(film));

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

        List<Film> films = jdbcTemplate.query(sql, this::makeFilm, count);

        loadGenres(films);

        return films;
    }

    @Override
    public Film removeLike(Long filmId, Long userId) {

        String sql = "DELETE FROM films_likes " +
                "WHERE film_id = ? AND user_id = ?";

        jdbcTemplate.update(sql, filmId, userId);

        return get(filmId).get();
    }

    @Override
    public Film createLike(Long filmId, Long userId) {

        String sql = "INSERT INTO films_likes (film_id, user_id) VALUES (?, ?)";

        jdbcTemplate.update(sql, filmId, userId);

        return get(filmId).get();
    }

    private Film makeFilm(ResultSet rs, int rowNum) throws SQLException {

        int id = rs.getInt("film_id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        LocalDate releaseDate = rs.getDate("release_date").toLocalDate();
        int duration = rs.getInt("duration");
        Mpa mpa = new Mpa(rs.getInt("mpa.mpa_id"), rs.getString("mpa.name"));

        return new Film(id, name, description, releaseDate, duration, mpa, new ArrayList<>());
    }

    private void loadGenres(List<Film> films) {

        String sqlGenres = "SELECT film_id, g2.* " +
                "FROM FILM_GENRE " +
                "JOIN genre g2 ON g2.genre_id = film_genre.genre_id " +
                "WHERE film_id IN (:ids)";

        List<Long> ids = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));

        SqlParameterSource parameters = new MapSqlParameterSource("ids", ids);

        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlGenres, parameters);

        while (sqlRowSet.next()) {
            long filmId = sqlRowSet.getLong("film_id");
            int genreId = sqlRowSet.getInt("genre_id");
            String name = sqlRowSet.getString("name");
            filmMap.get(filmId).getGenres().add(new Genre(genreId, name));
        }
        films.stream()
                .map(film -> film.getGenres().addAll(filmMap.get(film.getId()).getGenres()));
    }
}