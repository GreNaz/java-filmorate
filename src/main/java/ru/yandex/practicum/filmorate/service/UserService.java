package ru.yandex.practicum.filmorate.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.UserAlreadyExistException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.friends.FriendStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

/**
 * Responsible for such operations with users,
 * like adding to friends, removing from friends, displaying a list of mutual friends.
 * While users do not need to approve friend requests, we add them right away.
 * That is, if Lena became Sasha's friend, then this means that Sasha is now Lena's friend.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;
    private final FriendStorage friendStorage;

    public User addFriend(Long fromUser, Long toUser) {
        validation(fromUser, toUser);
        friendStorage.addFriend(fromUser, toUser);
        return get(fromUser);
    }

    public User deleteFriend(Long fromUser, Long toUser) {
        validation(fromUser, toUser);
        friendStorage.deleteFriend(fromUser, toUser);
        return get(fromUser);
    }

    public List<User> mutualFriends(Long fromUser, Long toUser) {
        validation(fromUser, toUser);
        return friendStorage.mutualFriends(fromUser, toUser);
    }

    public List<User> getFriends(Long id) {
        get(id);
        return friendStorage.getFriends(id);
    }

    public User create(User user) {
        validationNew(user);
        return userStorage.create(user);
    }

    public User update(User user) {
        validationNew(user);
        return userStorage.update(user);
    }

    public List<User> getUsers() {
        return userStorage.getUsers();
    }

    public User get(Long id) {
        return userStorage.get(id).orElseThrow(
                () -> new UserAlreadyExistException("User id = " + id + " was not found"));
    }

    private void validationNew(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Username equated to login: " + "name = " + user.getLogin());
        }

        if (userStorage.getUsers().contains(user)) {
            log.error("User with email " + user.getEmail() + " already registered.");
            throw new UserAlreadyExistException(String.format(
                    "User with email %s already registered.",
                    user.getEmail()));
        }
    }

    private void validation(Long fromUser, Long toUser) {
        get(fromUser);
        get(toUser);
    }
}
