package ru.yandex.practicum.filmorate.storage.friends;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface FriendStorage {
    List<User> getFriends(Long id);

    List<User> mutualFriends(Long fromUser, Long toUser);

    void deleteFriend(Long fromUser, Long toUser);

    void addFriend(Long fromUser, Long toUser);
}
