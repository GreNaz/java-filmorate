package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;
    private static final LocalDate FIRST_FILM_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Received a request to add a new film");
        validation(film);
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
        log.info("Received a request to update film with id {}", film.getId());
        validation(film);
        return filmService.update(film);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film createLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Received a request to add like to film with id: {}", filmId);
        return filmService.createLike(filmId, userId);
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Received a request to get all films");
        return filmService.getFilms();
    }

    @GetMapping("/{id}")
    public Film get(@PathVariable Long id) {
        log.info("Received a request to get film with id: {}", id);
        return filmService.get(id);
    }

    @GetMapping("/popular")
    public List<Film> getPopular(@RequestParam(defaultValue = "10") @Positive int count) {
        log.info("Received a request to get list of {} popular films", count);
        return filmService.getPopular(count);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable Long filmId, @PathVariable Long userId) {
        log.info("Received a request to remove like from film with id: {}", filmId);
        return filmService.removeLike(filmId, userId);
    }

    private void validation(Film film) {
        if (film.getReleaseDate().isBefore(FIRST_FILM_RELEASE_DATE)) {
            log.error("Realise date must be after :  {}", FIRST_FILM_RELEASE_DATE);
            throw new ValidationException("Realise date must be after :  " + FIRST_FILM_RELEASE_DATE);
        }
    }
}
