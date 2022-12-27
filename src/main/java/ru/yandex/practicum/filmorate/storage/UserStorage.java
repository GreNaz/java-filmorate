package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Optional;

public interface UserStorage {

    User create(User user);

    User update(User user);

    List<User> get();

    Optional<User> get(Long id);

    void delete(Long id);

    List<Long> geSimilar(Long userId);
}
