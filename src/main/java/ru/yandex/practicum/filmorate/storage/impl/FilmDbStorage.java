package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.FilmSortBy;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Repository
@RequiredArgsConstructor
public class FilmDbStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


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
    public List<Film> get() {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id";
        return jdbcTemplate.query(sql, Mapper::filmMapper);
    }

    @Override
    public List<Film> getByTitle(String query) {
        String sql = "SELECT films.*, m.* " +
                "FROM films " +
                "JOIN mpa m ON m.MPA_ID = films.mpa_id " +
                "WHERE LCASE(films.name) LIKE ?";

        return jdbcTemplate.query(sql, Mapper::filmMapper, "%" + query.toLowerCase() + "%");
    }

    @Override
    public List<Film> getByDirectorAndTitle(String query) {
        String sql = "SELECT * " +
                "FROM FILMS " +
                "LEFT JOIN mpa ON FILMS.MPA_ID = MPA.MPA_ID " +
                "LEFT JOIN film_director fd ON films.film_id = fd.FILM_ID " +
                "LEFT JOIN director d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LCASE(films.name)  LIKE ? OR LCASE(d.NAME) LIKE ? " +
                "ORDER BY FILMS.FILM_ID DESC ";

        String search = "%" + query.toLowerCase() + "%";

        return jdbcTemplate.query(sql, Mapper::filmMapper, search, search);
    }

    @Override
    public Optional<Film> get(Long id) {

        String sql = "SELECT FILMS.*, M.* " +
                "FROM FILMS " +
                "JOIN MPA M ON M.MPA_ID = FILMS.MPA_ID " +
                "WHERE FILMS.FILM_ID = ?";

        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);

        if (!filmRows.next()) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Mapper::filmMapper, id));
        } catch (DataAccessException dataAccessException) {
            return Optional.empty();
        }
    }

    @Override
    public List<Film> get(List<Long> id) {

        String query = "SELECT FILMS.*, M.* " +
                "FROM FILMS " +
                "JOIN MPA M ON M.MPA_ID = FILMS.MPA_ID " +
                "WHERE FILMS.FILM_ID IN (:id)";

        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        return namedParameterJdbcTemplate.query(query, parameters, Mapper::filmMapper);
    }

    @Override
    public List<Film> getPopular(int count) {

        String sql = "SELECT * " +
                "FROM FILMS " +
                "ORDER BY RATE DESC";

        return jdbcTemplate.query(sql, Mapper::filmMapper, count);
    }

    @Override
    public void delete(Long id) {
        final String findFilm = "DELETE FROM films WHERE film_id = ?";
        jdbcTemplate.update(findFilm, id);
    }

    @Override
    public List<Film> getCommon(Long userId, Long friendId) {

        String sql = "SELECT f2.*, M.*\n" +
                "FROM FILM_MARKS \n" +
                "join FILM_MARKS f ON f.FILM_ID = FILM_MARKS.FILM_ID\n" +
                "LEFT JOIN films f2 on f2.film_id = f.film_id\n" +
                "join MPA M on f2.mpa_id = M.MPA_ID\n" +
                "WHERE f.USER_ID = ?\n" +
                "AND FILM_MARKS.USER_ID = ?" +
                "ORDER BY RATE;";
        return jdbcTemplate.query(sql, Mapper::filmMapper, userId, friendId);
    }

    public List<Film> getPopularByYear(int year) {
        final String getPopularFilmByYear = "SELECT * " +
                "FROM films " +
                "LEFT JOIN FILM_MARKS fl ON films.film_id = fl.film_id " +
                "LEFT JOIN MPA M ON FILMS.MPA_ID = M.MPA_ID " +
                "WHERE EXTRACT(YEAR FROM release_date) = ? " +
                "GROUP BY films.film_id, fl.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC";
        return jdbcTemplate.query(getPopularFilmByYear, Mapper::filmMapper, year);
    }

    @Override
    public List<Film> getPopularByGenre(int genreId) {
        final String getPopularFilmByGenre = "SELECT * " +
                "FROM FILMS " +
                "LEFT JOIN FILM_GENRE FG ON FILMS.FILM_ID = FG.FILM_ID " +
                "LEFT JOIN GENRE G ON FG.GENRE_ID = G.GENRE_ID " +
                "LEFT JOIN FILM_MARKS FL ON FILMS.FILM_ID = FL.FILM_ID " +
                "LEFT JOIN MPA M ON FILMS.MPA_ID = M.MPA_ID " +
                "WHERE G.GENRE_ID = ? " +
                "GROUP BY FILMS.FILM_ID " +
                "ORDER BY COUNT(FL.FILM_ID) DESC";
        return jdbcTemplate.query(getPopularFilmByGenre, Mapper::filmMapper, genreId);
    }

    @Override
    public List<Film> getPopularByYearAndGenre(int year, int genreId) {
        final String getPopularFilmByYearAndGenre = "SELECT * " +
                "FROM films " +
                "LEFT JOIN FILM_MARKS fl ON films.film_id = fl.film_id " +
                "LEFT JOIN MPA M ON FILMS.MPA_ID = M.MPA_ID " +
                "LEFT JOIN FILM_GENRE FG ON FILMS.FILM_ID = FG.FILM_ID " +
                "LEFT JOIN GENRE G ON FG.GENRE_ID = G.GENRE_ID " +
                "WHERE EXTRACT(YEAR FROM release_date) = ? AND G.GENRE_ID = ? " +
                "GROUP BY films.film_id, fl.film_id " +
                "ORDER BY COUNT(fl.film_id) DESC";
        return jdbcTemplate.query(getPopularFilmByYearAndGenre, Mapper::filmMapper, year, genreId);
    }


    @Override
    public List<Long> getIdOfCommon(List<Long> usersId, Long userId, int count) {

        String sql = "SELECT DISTINCT fl_1.film_id " +
                "FROM FILM_MARKS AS fl_1 " +
                "WHERE fl_1.user_id IN (:usersId) " +
                "EXCEPT " +
                "SELECT fl_2.film_id " +
                "FROM FILM_MARKS AS fl_2 " +
                "WHERE fl_2.user_id = :userId " +
                "LIMIT :count";


        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("usersId", usersId)
                .addValue("userId", userId)
                .addValue("count", count);

        SqlRowSet sqlRowSet = namedParameterJdbcTemplate.queryForRowSet(sql, parameters);

        List<Long> filmId = new ArrayList<>();

        while (sqlRowSet.next()) {
            filmId.add(sqlRowSet.getLong("FILM_ID"));
        }
        return filmId;
    }

    @Override
    public List<Film> getByDirector(int id, FilmSortBy sortBy) {

        String sortType = "FILMS.FILM_ID";

        if (sortBy.name().equals(FilmSortBy.year.name())) {
            sortType = "FILMS.RELEASE_DATE";
        } else if (sortBy.name().equals(FilmSortBy.likes.name())) {
            sortType = "FILMS.RATE";
        }

        String getFilmByDirector = "SELECT * " +
                "FROM films " +
                "LEFT JOIN mpa m ON films.mpa_id = m.mpa_id " +
                "LEFT JOIN film_director fd ON films.film_id = fd.film_id " +
                "LEFT JOIN director d ON fd.director_id = d.director_id " +
                "WHERE d.director_id = ? " +
                "GROUP BY films.film_id " +
                "ORDER BY " + sortType;

        List<Film> films = jdbcTemplate.query(getFilmByDirector, Mapper::filmMapper, id);
        if (films.isEmpty()) {
            throw new ObjectNotFoundException("Film with director id " + id + " not found");
        }
        return films;
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
