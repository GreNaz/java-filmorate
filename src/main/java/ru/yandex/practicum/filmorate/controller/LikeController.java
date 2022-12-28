package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.LikeService;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/films")
public class LikeController {
    private final FilmService filmService;
    private final LikeService likeService;


//    @PutMapping("/{filmId}/like/{userId}")
//    public Film createLike(@PathVariable Long filmId,
//                           @PathVariable Long userId) {
//        log.info("Received a request to add like to film with id: {}", filmId);
//        return filmService.createLike(filmId, userId);
//    }
//
//    @DeleteMapping("/{filmId}/like/{userId}")
//    public Film removeLike(@PathVariable Long filmId,
//                           @PathVariable Long userId) {
//        log.info("Received a request to remove like from film with an id: {}", filmId);
//        return filmService.removeLike(filmId, userId);
//    }
}
