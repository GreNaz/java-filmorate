package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MarkControllerTest {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final FilmService filmService;

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

        // сделать чтобы рейтинг перерассчитывался корректно - это убьет тесты из девелопа, не очковать)
        assertEquals(filmStorage.get(firstTestFilm.getId()).get().getRate(), 5.0);
    }

    @Test
    void get() {
    }

    @Test
    void delete() {
    }
}