package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testFindUserById() {

        User user = new User(1L,
                "mail@mail.ru",
                "userLogin",
                "userName",
                LocalDate.now().minusYears(25));

        userStorage.create(user);
        Optional<User> userOptional = userStorage.get(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(testUser ->
                        assertThat(testUser).hasFieldOrPropertyWithValue("id", 1L)
                );
    }

    @Test
    void testGetUsers() {
        User user1 = new User(1L,
                "1mail@mail.ru",
                "userLogin1",
                "userName1",
                LocalDate.now().minusYears(25));
        User user2 = new User(2L,
                "2mail@mail.ru",
                "userLogin2",
                "userName2",
                LocalDate.now().minusYears(30));
        User user3 = new User(3L,
                "3mail@mail.ru",
                "userLogin3",
                "userName3",
                LocalDate.now().minusYears(40));

        userStorage.create(user1);
        userStorage.create(user2);
        userStorage.create(user3);

        List<User> users = userStorage.getUsers();

        assertThat(users.containsAll(List.of(user1, user2, user3))).isTrue();
    }

}
