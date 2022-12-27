package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


    @Override
    public Optional<Director> create(Director director) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("director")
                .usingGeneratedKeyColumns("director_id");
        String key = simpleJdbcInsert.executeAndReturnKey(director.toMap()).toString();
        return get(Integer.parseInt(key));
    }

    @Override
    public Director update(Director director) {
        String sql = "UPDATE director SET name = ? " +
                "WHERE director_id = ?";
        int updateResult = jdbcTemplate.update(sql,
                director.getName(),
                director.getId());
        if (updateResult == 0) {
            throw new UserAlreadyExistException("Director " + director + " was not found");
        }
        return director;
    }

    @Override
    public void delete(int id) {
        String sql = "DELETE FROM director " +
                "WHERE director_id = ?";
        int result = jdbcTemplate.update(sql, id);
        if (result == 0) {
            throw new UserAlreadyExistException("Director id = " + id + " was not found");
        }
    }

    @Override
    public List<Director> get() {
        String sql = "SELECT * FROM director";
        return jdbcTemplate.query(sql, Mapper::directorMapper);
    }

    @Override
    public Optional<Director> get(int id) {
        String sql = "SELECT * FROM director WHERE director_id = ?";
        SqlRowSet directorRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!directorRows.next()) {
            return Optional.empty();
        }
        Director director = jdbcTemplate.queryForObject(sql, Mapper::directorMapper, id);
        return Optional.ofNullable(director);
    }

    @Override
    public void load(List<Film> films) {

        String sqlDirectors = "SELECT film_id, d2.* " +
                "FROM film_director " +
                "JOIN director d2 ON d2.director_id = film_director.director_id " +
                "WHERE film_id IN (:filmsId)";

        List<Long> filmsId = films.stream()
                .map(Film::getId)
                .collect(Collectors.toList());

        Map<Long, Film> filmMap = films.stream()
                .collect(Collectors.toMap(Film::getId, film -> film, (a, b) -> b));

        SqlParameterSource parameters = new MapSqlParameterSource("filmsId", filmsId);

        SqlRowSet sqlRowSet = namedJdbcTemplate.queryForRowSet(sqlDirectors, parameters);

        while (sqlRowSet.next()) {
            Long filmId = sqlRowSet.getLong("film_id");
            int directorId = sqlRowSet.getInt("director_id");
            String name = sqlRowSet.getString("name");

            filmMap.get(filmId).getDirectors().add(new Director(directorId, name));
        }
    }

    @Override
    public List<Film> getByDirector(String query) {
        String sql = "SELECT * " +
                "FROM FILMS " +
                "LEFT JOIN mpa ON FILMS.MPA_ID = MPA.MPA_ID " +
                "LEFT JOIN film_director fd ON films.film_id = fd.FILM_ID " +
                "LEFT JOIN director d ON fd.DIRECTOR_ID = d.DIRECTOR_ID " +
                "WHERE LCASE(d.NAME) LIKE ?";
        return jdbcTemplate.query(sql, Mapper::filmMapper, "%" + query.toLowerCase() + "%");
    }
}
