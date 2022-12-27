package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MarksStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MarkControllerTest {

    private final FilmStorage filmStorage;
    private final FilmService filmService;
    private final MarksStorage marksStorage;
    private final UserStorage userStorage;
    private final MockMvc mockMvc;

    @Test
    @Order(1)
    void createMark() throws Exception {

        User firstTestUser = userStorage.create(User.builder()
                .email("firstTestUser@mail.ru")
                .login("firstTestUser")
                .name("firstTestUserName")
                .birthday(LocalDate.of(1998, 9, 19))
                .build());

        Film firstTestFilm = filmStorage.create(Film.builder()
                .name("firstTestFilm")
                .description("firstTestFilmDescription")
                .releaseDate(LocalDate.of(1900, 9, 19))
                .duration(160)
                .mpa(new Mpa(1, "PG"))
                .build());

        mockMvc.perform(put("/films/" + firstTestFilm.getId() + "/mark/" + firstTestUser.getId() + "?mark=5"))
                .andExpect(status().isOk());

        //проверка что рейтинг в базе пересчитывается корректно
        assertEquals(marksStorage.getRate(firstTestFilm.getId()), 5.0);
        //проверка что рейтинг при получении фильма пересчитывется корректно
        assertEquals(filmService.get(firstTestFilm.getId()).getRate(), 5.0);
    }

    @Test
    void get() {
    }

    @Test
    void delete() {
    }
}