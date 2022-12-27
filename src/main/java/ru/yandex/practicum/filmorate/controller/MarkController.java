package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.service.MarkService;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class MarkController {
    private final MarkService markService;

    @PutMapping("/{filmId}/mark/{userId}")
    public void create(@PathVariable Long filmId,
                       @PathVariable Long userId,
                       @RequestParam @Max(10) @Min(1) int mark) {
        log.info("Received a request to add mark to film with id: {} ", filmId);
        markService.create(filmId, userId, mark);
    }

    @GetMapping("/mark/{filmId}")
    public String get(@PathVariable Long filmId) {
        log.info("Received a request to get mark by film {} ", filmId);
        return markService.getByFilm(filmId);
    }

    @DeleteMapping("/{filmId}/mark/{userId}")
    public void delete(@PathVariable Long filmId,
                       @PathVariable Long userId) {
        markService.delete(filmId, userId);
    }
}
