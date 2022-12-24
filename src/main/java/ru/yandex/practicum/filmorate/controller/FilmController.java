package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

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
    public Film update(@Valid @RequestBody Film film) {
        log.info("Received a request to update film with id {}", film.getId());
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
        log.info("Received a request to remove like from film with an id: {}", filmId);
        return filmService.removeLike(filmId, userId);
    }

    @GetMapping("/common")
    public List<Film> commonFilms(@RequestParam Long userId,
                                  @RequestParam Long friendId) {
        log.info("Received a request to get common films between users with an id-s: {} and {}", userId, friendId);
        return filmService.commonFilms(userId, friendId);
    }

    @GetMapping("/director/{directorId}")
    public List<Film> getFilmsByDirector(@PathVariable int directorId, @RequestParam String sortBy) {
        log.info("Received a request to get list of {} popular films", directorId);
        return filmService.getFilmsByDirectorWithSort(directorId, sortBy);
    }

    @DeleteMapping("/{id}")
    public void deleteFilm(@PathVariable Long id) {
        filmService.deleteById(id);
        log.info("Был удален фильм с id {}", id);
    }

    @GetMapping("/search")
    public List<Film> getFilmsByDirector(@RequestParam String query, @RequestParam String by) {
        log.info("Received a request to search list of popular films with substring {} and with director/title {}", query, by);
        return filmService.searchFilmsByDirectorAndTitle(query, by);
    }


    @GetMapping(value = "/popular", params = {"year"})
    public List<Film> getPopularFilmByYear(@RequestParam Integer year) {
        log.info("Received a request for a list popular film in {}", year);
        return filmService.getPopularFilmByYear(year);
    }

    @GetMapping(value = "/popular", params = {"genreId"})
    public List<Film> getPopularFilmByGenre(@RequestParam Integer genreId) {
        log.info("Received a request for a list popular film by genre {}", genreId);
        return filmService.getPopularFilmByGenre(genreId);
    }

    @GetMapping(value = "/popular", params = {"year", "genreId"})
    public List<Film> getPopularFilmByYearAndGenre(@RequestParam Integer year,
                                                   @RequestParam Integer genreId) {
        log.info("Received a request for a list popular film in {} and genre {}", year, genreId);
        return filmService.getPopularFilmByYearAndGenre(year, genreId);
    }
}
