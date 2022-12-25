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
import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.exception.AlreadyExistException;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.director.impl.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.*;

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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FilmControllerTest {
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final DirectorDbStorage directorStorage;
    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    final FilmService filmService;

    @Test
    void findAllTest() throws Exception {
        filmStorage.create(FILM);
        mockMvc.perform(
                        get("/films")
                )
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
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
    @Order(2)
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
    @Order(3)
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
    @Order(4)
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
    @Order(5)
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
    @Order(6)
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
    @Order(7)
    void addLikeNotFoundExceptionTest() throws Exception {

        mockMvc.perform(
                        put("/films/29/like/2")
                )
                .andExpect(status().isNotFound())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof DataAccessException));
    }

    @Test
    @Order(8)
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
    @Order(9)
    @DirtiesContext
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

    @Test
    @Order(10)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getFilmsByDirectorWithoutSort() {
        LinkedHashSet<Genre> listGenres = new LinkedHashSet<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        LinkedHashSet<Director> listDirectors = new LinkedHashSet<>();
        listDirectors.add(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        final Film filmWithDirector = Film.builder().name("Film").description("Фильм первый")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors)
                .build();
        final Film filmWithoutDirector = Film.builder().name("Film").description("Фильм первый")
                .releaseDate(LocalDate.of(201, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(new LinkedHashSet<>())
                .build();
        filmStorage.create(filmWithDirector);
        filmStorage.create(filmWithoutDirector);
        assertEquals(filmService.getFilmsByDirectorWithSort(1, "year"), List.of(filmService.get(1L)));
    }

    @Test
    @Order(11)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getFilmsByDirectorWithSortByYear() {
        LinkedHashSet<Genre> listGenres = new LinkedHashSet<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        LinkedHashSet<Director> listDirectors = new LinkedHashSet<>();
        listDirectors.add(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        final Film filmWithDirector1 = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2010, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors)
                .build();
        final Film filmWithDirector2 = Film.builder().name("Film2").description("Фильм второй")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors)
                .build();
        final Film filmWithoutDirector = Film.builder().name("Film3").description("Фильм третий")
                .releaseDate(LocalDate.of(201, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(new LinkedHashSet<>())
                .build();
        filmStorage.create(filmWithDirector1);
        filmStorage.create(filmWithDirector2);
        filmStorage.create(filmWithoutDirector);
        assertEquals(filmService.getFilmsByDirectorWithSort(1, "year"), List.of(filmService.get(2L), filmService.get(1L)));
    }

    @Test
    @Order(12)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void getFilmsByDirectorWithSortByLikes() {
        LinkedHashSet<Genre> listGenres = new LinkedHashSet<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        LinkedHashSet<Director> listDirectors = new LinkedHashSet<>();
        listDirectors.add(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        final Film filmWithDirector11 = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2010, 1, 1)).duration(120)
                .rate(10).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors)
                .build();
        final Film filmWithDirector22 = Film.builder().name("Film2").description("Фильм второй")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(4).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors)
                .build();
        final Film filmWithoutDirector = Film.builder().name("Film3").description("Фильм третий")
                .releaseDate(LocalDate.of(201, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(new LinkedHashSet<>())
                .build();
        filmStorage.create(filmWithDirector11);
        filmStorage.create(filmWithDirector22);
        filmStorage.create(filmWithoutDirector);
        assertEquals(filmService.getFilmsByDirectorWithSort(1, "likes"), List.of(filmService.get(2L), filmService.get(1L)));
    }

    @Test
    @Order(13)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void searchFilmsByDirector() {
        LinkedHashSet<Genre> listGenres = new LinkedHashSet<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        LinkedHashSet<Director> listDirectors1 = new LinkedHashSet<>();
        listDirectors1.add(Director.builder().id(1).name("Director1").build());
        LinkedHashSet<Director> listDirectors2 = new LinkedHashSet<>();
        listDirectors2.add(Director.builder().id(2).name("Director2").build());
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(2).name("Director2").build());
        final Film filmWithDirector1 = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2010, 1, 1)).duration(120)
                .rate(10).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors1)
                .build();
        final Film filmWithDirector2 = Film.builder().name("Film2").description("Фильм второй")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(4).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors2)
                .build();
        final Film filmWithoutDirector = Film.builder().name("Film3").description("Фильм третий")
                .releaseDate(LocalDate.of(201, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(new LinkedHashSet<>())
                .build();
        filmStorage.create(filmWithDirector1);
        filmStorage.create(filmWithDirector2);
        filmStorage.create(filmWithoutDirector);
        assertEquals(filmService.searchFilmsByDirectorAndTitle("Dir", "director"), List.of(filmService.get(1L), filmService.get(2L)));
        assertEquals(filmService.searchFilmsByDirectorAndTitle("diRector2", "director"), List.of(filmService.get(2L)));
    }

    @Test
    @Order(14)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void searchFilmsByTitle() {
        LinkedHashSet<Genre> listGenres = new LinkedHashSet<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        LinkedHashSet<Director> listDirectors1 = new LinkedHashSet<>();
        listDirectors1.add(Director.builder().id(1).name("Director1").build());
        LinkedHashSet<Director> listDirectors2 = new LinkedHashSet<>();
        listDirectors2.add(Director.builder().id(2).name("Director2").build());
        directorStorage.create(Director.builder().id(1).name("Director1").build());
        directorStorage.create(Director.builder().id(2).name("Director2").build());
        final Film filmWithDirector1 = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2010, 1, 1)).duration(120)
                .rate(10).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors1)
                .build();
        final Film filmWithDirector2 = Film.builder().name("Film2").description("Фильм второй")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(4).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors2)
                .build();
        final Film filmWithoutDirector = Film.builder().name("Film3").description("Фильм третий")
                .releaseDate(LocalDate.of(201, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(new LinkedHashSet<>())
                .build();
        filmStorage.create(filmWithDirector1);
        filmStorage.create(filmWithDirector2);
        filmStorage.create(filmWithoutDirector);
        assertEquals(filmService.searchFilmsByDirectorAndTitle("fiLM1", "title"), List.of(filmService.get(1L)));
        assertEquals(filmService.searchFilmsByDirectorAndTitle("fiLM", "title"), List.of(filmService.get(1L), filmService.get(2L), filmService.get(3L)));
    }

    @Test
    @Order(15)
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    void searchFilmsByDirectorAndTitle() {
        LinkedHashSet<Genre> listGenres = new LinkedHashSet<>();
        listGenres.add(Genre.builder().id(1).name("Комедия").build());
        LinkedHashSet<Director> listDirectors1 = new LinkedHashSet<>();
        listDirectors1.add(Director.builder().id(1).name("Director1").build());
        LinkedHashSet<Director> listDirectors2 = new LinkedHashSet<>();
        listDirectors2.add(Director.builder().id(2).name("Director2").build());
        directorStorage.create(Director.builder().id(1).name("Director superfilm1").build());
        directorStorage.create(Director.builder().id(2).name("Director2").build());
        final Film filmWithDirector1 = Film.builder().name("Film1").description("Фильм первый")
                .releaseDate(LocalDate.of(2010, 1, 1)).duration(120)
                .rate(10).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors1)
                .build();
        final Film filmWithDirector2 = Film.builder().name("Film2").description("Фильм второй")
                .releaseDate(LocalDate.of(2000, 1, 1)).duration(120)
                .rate(4).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(listDirectors2)
                .build();
        final Film filmWithoutDirector = Film.builder().name("Superfilm13").description("Фильм третий")
                .releaseDate(LocalDate.of(201, 1, 1)).duration(120)
                .rate(1).mpa(Mpa.builder().id(1).name("G").build())
                .genres(listGenres)
                .directors(new LinkedHashSet<>())
                .build();
        filmStorage.create(filmWithDirector1);
        filmStorage.create(filmWithDirector2);
        filmStorage.create(filmWithoutDirector);
        assertEquals(filmService.searchFilmsByDirectorAndTitle("diRector2", "title,director"), List.of(filmService.get(2L)));
        assertEquals(filmService.searchFilmsByDirectorAndTitle("superFILM", "title,director"), List.of(filmService.get(1L), filmService.get(3L)));
    }
}
