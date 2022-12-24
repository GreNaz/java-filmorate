package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.impl.DirectorDbStorage;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorDbStorage directorDbStorage;

    public Director createDirector(Director director) {
        log.info("Create director");
        return directorDbStorage.create(director).orElseThrow(() ->
                new ObjectNotFoundException("Error creating director"));
    }

    public Director updateDirector(Director director) {
        log.info("Update director");
        return directorDbStorage.update(director);
    }

    public void deleteDirector(int id) {
        log.info("Delete director with id" + id);
        directorDbStorage.delete(id);
    }

    public List<Director> getDirectors() {
        log.info("Send director list");
        return directorDbStorage.getDirectors();
    }

    public Director get(int id) {
        log.info("Send director with id: " + id);
        return directorDbStorage.get(id).orElseThrow(
                () -> new ObjectNotFoundException("Director id : " + id + " not found")
        );
    }
}
