package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.MarkService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class MarkController {
    private final MarkService markService;

    @PutMapping("/{filmId}/mark/{userId}")
    public Film create(@PathVariable Long filmId,
                       @PathVariable Long userId,
                       @RequestParam @Max(10) @Min(1) int mark) {
        log.info("Received a request to add mark to film with id: {} ", filmId);
        return markService.create(filmId, userId, mark);
    }

    @DeleteMapping("/{filmId}/mark/{userId}")
    public void delete(@PathVariable Long filmId,
                       @PathVariable Long userId) {
        markService.delete(filmId, userId);
    }

    @GetMapping("{filmId}/mark")
    public String getMarkByFilm(@PathVariable Long filmId) {
        log.info("Received a request to get mark from a movie {} ", filmId);
        return String.format("%.1f", markService.getByFilm(filmId));
    }

    @GetMapping("/mark/{userId}")
    public List<Film> getMarksByUser(@PathVariable Long userId) {
        log.info("Received a request to get a list marks by user {}", userId);
        return markService.getMarksByUser(userId);
    }
}
