package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.helper.TestDataHelper.*;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase

public class FilmControllerTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final FilmService filmService;

    @Test
    void findAllTest() throws Exception {
        filmStorage.create(FILM);
        mockMvc.perform(
                        get("/films")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addFilm() throws Exception {
        mockMvc.perform(
                        post("/films")
                                .content(objectMapper.writeValueAsString(FILM))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    void filmUpdateTest() throws Exception {
        filmStorage.create(FILM_2);

        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(FILM))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/films/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120));
    }

    @Test
    void filmNotFoundForUpdateTest() throws Exception {
        FILM.setId(7);
        mockMvc.perform(
                        put("/films")
                                .content(objectMapper.writeValueAsString(FILM))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistException))
                .andExpect(result -> assertEquals("NOT FOUND FILM: Film(id=7, name=Film, description=good film, releaseDate=2020-05-05, duration=120, rate=1, mpa=Mpa(id=1, name=G), genres=[], directors=[])",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getFilmByIdTest() throws Exception {

        mockMvc.perform(
                        get("/films/1")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Film"))
                .andExpect(jsonPath("$.description").value("good film"))
                .andExpect(jsonPath("$.releaseDate").value("2020-05-05"))
                .andExpect(jsonPath("$.duration").value(120))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getFilmByIdNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        get("/films/18")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof AlreadyExistException))
                .andExpect(result -> assertEquals("Film id = 18 was not found",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void addLikeAndRemoveTest() throws Exception {
        USER.setEmail("test4@test.ru");
        USER.setLogin("login4");
        userStorage.create(USER);
        filmStorage.create(FILM);

        mockMvc.perform(
                        put("/films/1/like/1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        delete("/films/1/like/1")
                )
                .andExpect(status().isOk());
    }

    @Test
    void addLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        put("/films/29/like/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataAccessException));
    }

    @Test
    void removeLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        delete("/films/17/like/24")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ObjectNotFoundException))
                .andExpect(result -> assertEquals("Not found film or user",
                        Objects.requireNonNull(result.getResolvedException()).getMessage()));
    }

    @Test
    void getBestFilmTest() throws Exception {
        FILM_2.setId(2);
        filmStorage.update(FILM_2);

        mockMvc.perform(
                        put("/films/2/like/1")
                )
                .andExpect(status().isOk());

        mockMvc.perform(
                        get("/films/popular")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(filmStorage.getPopular(2).get(0).getName(), "2 Film"))
                .andExpect(result -> assertEquals(filmStorage.getPopular(2).get(1).getName(), "Film"));
    }

//    @Test
//    void commonFilmsTest() throws Exception {
//
//        filmStorage.create(FILM);
//        filmService.createLike(1L, 1L);
//        filmService.createLike(1L, 2L);
//        mockMvc.perform(
//                        get("/films/common")
//                )
//                .andExpect(result -> assertEquals(filmService.commonFilms(1L, 2L).size(), 1))
//                .andExpect(result -> assertEquals(filmService.commonFilms(1L, 2L).get(0).getId(), 1))
//                .andExpect(result -> assertEquals(filmService.commonFilms(1L, 2L).get(0).getName(), "Film"))
//                .andExpect(result -> assertEquals(filmService.commonFilms(1L, 2L).get(0).getDescription(), "good film"));
//    }
}
