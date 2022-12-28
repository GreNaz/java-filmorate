package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.Update;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.FilmSearchBy;
import ru.yandex.practicum.filmorate.model.dictionary.FilmSortBy;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        log.info("Received a request to add a new film");
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Validated(Update.class) @RequestBody Film film) {
        log.info("Received a request to update film with id {}", film.getId());
        return filmService.update(film);
    }

    @GetMapping
    public List<Film> get() {
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

    @GetMapping("/common")
    public List<Film> commonFilms(@RequestParam Long userId,
                                  @RequestParam Long friendId) {
        log.info("Received a request to get common films between users with an id-s: {} and {}", userId, friendId);
        return filmService.commonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getBy(@PathVariable int directorId,
                            @RequestParam FilmSortBy sortBy) {
        log.info("Received a request to get list of {} popular films", directorId);
        return filmService.getFilmsByDirectorWithSort(directorId, sortBy);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        filmService.delete(id);
        log.info("Film with an id {} was deleted", id);
    }

    @GetMapping("/search")
    public List<Film> getBy(@RequestParam String query,
                            @RequestParam String by) {
        log.info("Received a request to search list of popular films with substring {} and with director/title {}", query, by);
        Set<FilmSearchBy> filteredBy = Arrays.stream(by.split(","))
                .map(FilmSearchBy::valueOf).collect(Collectors.toSet());

        return filmService.getByDirector(query, filteredBy);
    }


    @GetMapping(value = "/popular", params = {"year"})
    public List<Film> getPopularFilmByYear(@RequestParam @Min(1895) Integer year) {
        log.info("Received a request for a list popular film in {}", year);
        return filmService.getPopularFilmByYear(year);
    }

    @GetMapping(value = "/popular", params = {"genreId"})
    public List<Film> getPopularFilmByGenre(@RequestParam Integer genreId) {
        log.info("Received a request for a list popular film by genre {}", genreId);
        return filmService.getPopularFilmByGenre(genreId);
    }

    @GetMapping(value = "/popular", params = {"year", "genreId"})
    public List<Film> getPopularFilmByYearAndGenre(@RequestParam @Min(1895) Integer year,
                                                   @RequestParam Integer genreId) {
        log.info("Received a request for a list popular film in {} and genre {}", year, genreId);
        return filmService.getPopularFilmByYearAndGenre(year, genreId);
    }
}
