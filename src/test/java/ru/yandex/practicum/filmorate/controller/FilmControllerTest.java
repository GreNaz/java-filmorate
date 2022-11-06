package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmControllerTest {

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);
    private static final FilmController filmcontroller = new FilmController();

    @Test
    void checkEARLIEST_DATE() {
        Film notCorrectDateFilm = Film.builder()
                .id(1)
                .description("desc")
                .name("name")
                .duration(100)
                .releaseDate(EARLIEST_DATE.minusYears(1))
                .build();
        assertThrows(ValidationException.class, () -> filmcontroller.addFilm(notCorrectDateFilm),
                "Дата релиза должна быть — не раньше : " + EARLIEST_DATE);
    }

    @Test
    void checkDuplicates() {
        Film correctFilm = Film.builder()
                .id(1)
                .description("desc")
                .name("name")
                .duration(100)
                .releaseDate(EARLIEST_DATE)
                .build();
        filmcontroller.addFilm(correctFilm);
        assertThrows(ValidationException.class, () -> filmcontroller.addFilm(correctFilm),
                "Фильм " + correctFilm.getName() + " уже зарегистрирован.");
    }

}