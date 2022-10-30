package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController

@RequestMapping("/films")

public class FilmController {

    private int id;

    private static final LocalDate EARLIEST_DATE = LocalDate.of(1895, 12, 28);

    private final Map<Integer, Film> films = new HashMap<>();

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        validationData(film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Добавлен новый фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        validationData(film);
        films.put(film.getId(), film);
        log.info("Фильм {}, обновлен: {}", film.getName(), film);
        return film;
    }

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    private void validationData(Film film) {

        if (film.getReleaseDate().isBefore(EARLIEST_DATE)) {
            log.error("Дата релиза раньше {}", EARLIEST_DATE);
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }

        if (films.containsValue(film)) {
            log.error("Фильм " + film.getName() + " уже зарегистрирован.");
            throw new ValidationException("Фильм " +
                    film.getName() + " уже зарегистрирован.");
        }
    }
}
