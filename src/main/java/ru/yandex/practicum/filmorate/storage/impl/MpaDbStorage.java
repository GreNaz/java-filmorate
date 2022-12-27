package ru.yandex.practicum.filmorate.storage.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;
import ru.yandex.practicum.filmorate.storage.mapper.Mapper;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> get() {
        String sql = "SELECT * FROM mpa";
        return jdbcTemplate.query(sql, Mapper::mpaMapper);
    }

    @Override
    public Optional<Mpa> get(int id) {
        String sql = "SELECT * FROM mpa WHERE MPA_ID = ?";
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!mpaRows.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Mapper::mpaMapper, id));
    }

    public void load(Film film) {
        film.setMpa(jdbcTemplate.queryForObject("SELECT * FROM mpa WHERE MPA_ID = ?",
                Mapper::mpaMapper, film.getMpa().getId()));
    }
}