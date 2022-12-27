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
//@AutoConfigureTestDatabase
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

        User firstTestUser = userStorage.create(
                new User(10,
                        "firstTestUser@mail.ru",
                        "firstTestUser",
                        "firstTestUserName",
                        LocalDate.of(1998, 9, 19)
                ));

        Film firstTestFilm = filmStorage.create(
                new Film(10,
                        "firstTestFilm",
                        "firstTestFilmDescription",
                        LocalDate.of(1900, 9, 19),
                        160,
                        null,
                        new Mpa(1, "PG"),
                        null,
                        null
                ));

        mockMvc.perform(put("/films/10/mark/10?mark=5"))
                .andExpect(status().isOk());
    }

    @Test
    void get() {
    }

    @Test
    void delete() {
    }
}