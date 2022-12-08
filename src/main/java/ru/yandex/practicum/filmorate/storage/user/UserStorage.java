package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface UserStorage {

    User create(User user); // добавление нового пользователя

    User update(User user); // модификация существующего пользователя

    List<User> getUsers(); // запрос списка всех пользователей

    Optional<User> get(Long id);

    Set<User> getFriends(Long id);

    List<User> mutualFriends(Long fromUser, Long toUser);

    User deleteFriend(Long fromUser, Long toUser);

    User addFriend(Long fromUser, Long toUser);
}
