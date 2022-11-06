package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserControllerTest {

    private static final UserController userController = new UserController();

    @Test
    void checkNameLogin() {
        User userWithoutName = User.builder()
                .id(1)
                .name("")
                .login("NaGiBaToR9817")
                .birthday(LocalDate.now().minusYears(10))
                .email("NaGiBaToR9817@mail.ru")
                .build();
        userController.create(userWithoutName);

        assertEquals(userWithoutName.getLogin(), userController.getUser(1).getName());
    }

    @Test
    void checkDuplicates() {
        User user = User.builder()
                .id(1)
                .name("")
                .login("test")
                .birthday(LocalDate.now().minusYears(10))
                .email("test@mail.ru")
                .build();

        userController.create(user);

        assertThrows(ValidationException.class, () -> userController.create(user),
                "Пользователь с электронной почтой " + user.getEmail() + " уже зарегистрирован.");
    }
}