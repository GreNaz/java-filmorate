package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.storage.util.mapper.Mapper;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> get() {
        String sql = "select * from USERS";
        return jdbcTemplate.query(sql, Mapper::userMapper);
    }

    @Override
    public User create(User user) {
        String sql = "INSERT INTO users (email, login, name, birthday) " +
                "VALUES ( ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"user_id"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        return user;
    }

    @Override
    public User update(User user) {
        String sql = "UPDATE users SET EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?";

        int updateResult = jdbcTemplate.update(sql,
                user.getEmail(),
                user.getLogin(),
                user.getName(),
                user.getBirthday(),
                user.getId());

        if (updateResult == 0) {
            throw new UserAlreadyExistException("User " + user + " was not found");
        }

        return user;
    }

    @Override
    public Optional<User> get(Long id) {
        String sql = "select * from USERS where USER_ID = ?";
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet(sql, id);
        if (!filmRows.next()) {
            return Optional.empty();
        }
        return Optional.ofNullable(jdbcTemplate.queryForObject(sql, Mapper::userMapper, id));
    }

    @Override
    public void delete(Long id) {
        final String deleteUser = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(deleteUser, id);
    }

    @Override
    public List<Long> geSimilar(Long userId) {
        String sql = "SELECT FL2.USER_ID\n" +
                "FROM FILMS_LIKES AS FL1\n" +
                "         JOIN FILMS_LIKES AS FL2 ON FL1.FILM_ID = FL2.FILM_ID\n" +
                "WHERE FL1.USER_ID = ?\n" +
                "  AND FL1.USER_ID <> FL2.USER_ID\n" +
                "GROUP BY FL1.USER_ID, FL2.USER_ID\n" +
                "ORDER BY count(FL1.FILM_ID) DESC";
        return jdbcTemplate.queryForList(sql, Long.class, userId);
    }
}