package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static ru.yandex.practicum.filmorate.helper.TestDataHelper.CHECK_MPA;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
public class MpaControllerTest {

    private final MockMvc mockMvc;

    @Autowired
    public MpaControllerTest(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @Test
    void getMpaByIdTest() throws Exception {
        mockMvc.perform(
                        get("/mpa/2")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("PG"));
    }

    @Test
    void getAllMpaRatesTest() throws Exception {
        mockMvc.perform(
                        get("/mpa")
                )
                .andExpect(status().isOk())
                .andExpect(result -> assertEquals(CHECK_MPA, result.getResponse().getContentAsString()));
    }
}