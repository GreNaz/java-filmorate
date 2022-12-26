package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    List<User> get(Long id);

    List<User> getCommon(Long fromUser, Long toUser);

    void delete(Long fromUser, Long toUser);

    void add(Long fromUser, Long toUser);
}
