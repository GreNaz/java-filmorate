package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.yandex.practicum.filmorate.helper.TestDataHelper.CHECK_GENRES;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
public class GenreControllerTest {

    private final MockMvc mockMvc;

    @Test
    void getGenreByIdTest() throws Exception {
        mockMvc.perform(
                        get("/genres/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Драма"));
    }

    @Test
    void getAllGenresTest() throws Exception {
        mockMvc.perform(
                        get("/genres")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(CHECK_GENRES, result.getResponse().
                        getContentAsString(StandardCharsets.UTF_8)));
    }
}