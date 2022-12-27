package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.dictionary.EventOperation;
import ru.yandex.practicum.filmorate.storage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class LikeService {
//    private final FilmStorage filmStorage;
//    private final LikeStorage likeStorage;
//    private final EventStorage eventStorage;
//
//    public Film createLike(Long filmId, Long userId) {
//        log.info("Adding a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
//        likeStorage.create(filmId, userId);
//        Event event = new Event(userId, EventType.LIKE, EventOperation.ADD, filmId);
//        eventStorage.create(event);
//        log.info("Added the 'Like' event.");
//        updateFilmRate(filmId);
//        return filmStorage.get(filmId).get();
//    }
//
//    private void updateFilmRate(Long filmId) {
//        Film film = filmStorage.get(filmId).orElseThrow(() ->
//                new ObjectNotFoundException("Updated error, film not found"));
//        film.setRate(likeStorage.getRate(filmId));
//        log.info("The rating of the film {} has been updated", filmId);
//    }
//
//    public Film removeLike(Long filmId, Long userId) {
//        log.info("Removing a like to a movie with an id = {} from a user with an id = {}", filmId, userId);
//        likeStorage.delete(filmId, userId);
//        Event event = new Event(userId, EventType.LIKE, EventOperation.REMOVE, filmId);
//        eventStorage.create(event);
//        log.info("Added the 'Delete Like' event.");
//        updateFilmRate(filmId);
//        return filmStorage.get(filmId).get();
//    }
}
