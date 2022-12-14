package ru.yandex.practicum.filmorate.storage.film.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.impl.UserDbStorage;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class FilmDbStorageTest {
    private final UserDbStorage userStorage;

    @Test
    void testFindUserById() {
        User user = new User(1,"mail@mail.ru","userLogin","userName", LocalDate.now().minusYears(25));
        userStorage.create(user);
        Optional<User> userOptional = userStorage.get(1L);

        assertThat(userOptional)
                .isPresent()
                .hasValueSatisfying(testUser ->
                        assertThat(testUser).hasFieldOrPropertyWithValue("id", 1L)
                );
    }
}
