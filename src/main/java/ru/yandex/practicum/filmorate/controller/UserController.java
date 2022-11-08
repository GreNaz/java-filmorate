package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    private long id;

    @PostMapping
    public User create(@Valid @RequestBody User user) {
        validationData(user);
        user.setId(++id);
        users.put(user.getId(), user);
        return user;
    }

    @PutMapping
    public User put(@Valid @RequestBody User user) {
        validationData(user);
        users.put(user.getId(), user);
        return user;
    }

    @GetMapping
    public Collection<User> findAll() {
        return users.values();
    }

    private void validationData(User user) {

        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя пользователя приравнено к логину: " + "name = " + user.getLogin());
        }

        if (users.containsValue(user)) {
            log.error("Пользователь с электронной почтой " + user.getEmail() + " уже зарегистрирован.");
            throw new ValidationException("Пользователь с электронной почтой " +
                    user.getEmail() + " уже зарегистрирован.");
        }
    }


    //пришлось добавить для теста
    public User getUser(long id) {
        return users.get(id);
    }
}