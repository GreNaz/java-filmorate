package ru.yandex.practicum.filmorate.storage.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id;

    @Override
    public User create(User user) {
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getUsers() {
        return List.copyOf(users.values());
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User addFriend(Long fromUser, Long toUser) {

        User user1 = users.get(fromUser);
        User user2 = users.get(toUser);

        if (user1.addFriend(toUser) == user1.getFriends()) {
            log.warn("Users already friends");
        }

        user1.addFriend(toUser);
        user2.addFriend(fromUser);

        log.info("User "
                + user2.getEmail()
                + " add to friends user "
                + user1.getEmail());

        return user1;
    }

    @Override
    public User deleteFriend(Long fromUser, Long toUser) {

        User user1 = users.get(fromUser);
        User user2 = users.get(toUser);

        user1.getFriends().remove(toUser);
        user2.getFriends().remove(fromUser);

        log.info("User "
                + user2
                + " remove from friends user "
                + user1);

        return user1;
    }

    @Override
    public List<User> mutualFriends(Long fromUser, Long toUser) {

        User user1 = users.get(fromUser);
        User user2 = users.get(toUser);

        // if any user hasn't friends than no mutual friends
        if (user1.getFriends() == null
                || user2.getFriends() == null) {
            return Collections.emptyList();
        }

        return user1.getFriends().stream()
                .filter(user2.getFriends()::contains)
                .map(users::get)
                .collect(Collectors.toList());
    }

    @Override
    public Set<User> getFriends(Long id) {
        return users.get(id).getFriends().stream()
                .map(users::get)
                .collect(Collectors.toSet());
    }
}
