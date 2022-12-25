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
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.impl.DirectorDbStorage;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@AutoConfigureTestDatabase
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DirectorControllerTest {
    private final DirectorDbStorage directorStorage;

    @Test
    @Order(1)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void createAndGetDirectorByIdTest() {
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        assertEquals(directorStorage.get(1).get(), Director.builder().id(1).name("Director1").build());
    }

    @Test
    @Order(2)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void updateGenresTest() {
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        directorStorage.update(Director.builder().id(1).name("Director Updated").build());
        assertEquals(directorStorage.get(1).get(), Director.builder().id(1).name("Director Updated").build());
    }

    @Test
    @Order(3)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getDirectors() {
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(2).name("Director2").build());
        assertEquals(directorStorage.getDirectors().size(), 2);
    }

    @Test
    @Order(4)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void deleteGenresTest() {
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(2).name("Director2").build());
        directorStorage.delete(1);
        directorStorage.delete(2);
        assertEquals(directorStorage.getDirectors().size(), 0);
    }
}
