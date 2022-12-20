package ru.yandex.practicum.filmorate.storage.director.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.util.mapper.Mapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    @Override
    public Director create(Director director) {
        String sql = "INSERT INTO director (director_id, name) " +
                "VALUES ( ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"director_id"});
            stmt.setString(1, director.getName());
            return stmt;
        }, keyHolder);
        director.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return director;
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
        jdbcTemplate.update(sql, id);
    }

    @Override
    public List<Director> getDirectors() {
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
}
