package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> getUsers();

    Optional<User> get(Long id);

    List<User> getFriends(Long id);

    List<User> mutualFriends(Long fromUser, Long toUser);

    User deleteFriend(Long fromUser, Long toUser);

    User addFriend(Long fromUser, Long toUser);
}
