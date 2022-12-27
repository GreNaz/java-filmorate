package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.impl.GenreDbStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreDbStorage genreDbStorage;

    public List<Genre> get() {
        log.info("Send genre list");
        return genreDbStorage.get();
    }

    public Genre get(int id) {
        log.info("Send genre with id = {}", id);
        return genreDbStorage.get(id).orElseThrow(
                () -> new ObjectNotFoundException("Genre id : " + id + " not found")
        );
    }
}