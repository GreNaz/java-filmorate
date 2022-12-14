package ru.yandex.practicum.filmorate.storage.friends.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class FriendDbStorage implements FriendStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<User> getFriends(Long id) {
        String sql = "SELECT USERS.USER_ID, email, login, name, birthday " +
                "FROM USERS " +
                "LEFT JOIN friendship f on users.USER_ID = f.friend_id " +
                "where f.user_id = ?";
        return jdbcTemplate.query(sql, UserDbStorage::makeUser, id);
    }

    @Override
    public void addFriend(Long followingId, Long followerId) {
        String sqlForWrite = "INSERT INTO FRIENDSHIP (USER_ID, FRIEND_ID) " +
                "VALUES (?, ?)";
        jdbcTemplate.update(sqlForWrite, followingId, followerId);
    }

    @Override
    public void deleteFriend(Long followingId, Long followerId) {
        String sql = "DELETE FROM FRIENDSHIP WHERE USER_ID = ? AND FRIEND_ID = ?";
        jdbcTemplate.update(sql, followingId, followerId);
    }

    @Override
    public List<User> mutualFriends(Long firstId, Long secondId) {
        String sql = "SELECT u.USER_ID, email, login, name, birthday " +
                "FROM friendship AS f " +
                "LEFT JOIN users u ON u.USER_ID = f.friend_id " +
                "WHERE f.user_id = ? AND f.friend_id IN ( " +
                "SELECT friend_id " +
                "FROM friendship AS f " +
                "LEFT JOIN users AS u ON u.USER_ID = f.friend_id " +
                "WHERE f.user_id = ? )";

        return jdbcTemplate.query(sql, UserDbStorage::makeUser, firstId, secondId);
    }

}
