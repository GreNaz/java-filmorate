package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorService {
    private final DirectorStorage directorDbStorage;

    public Director create(Director director) {
        log.info("Create director");
        return directorDbStorage.create(director).orElseThrow(()
                -> new ObjectNotFoundException("Error in creating processes"));
    }

    public Director update(Director director) {
        log.info("Update director");
        return directorDbStorage.update(director);
    }

    public void delete(int id) {
        log.info("Delete director with id" + id);
        directorDbStorage.delete(id);
    }

    public List<Director> get() {
        log.info("Send director list");
        return directorDbStorage.get();
    }

    public Director get(int id) {
        log.info("Send director with id: " + id);
        return directorDbStorage.get(id).orElseThrow(
                () -> new ObjectNotFoundException("Director id : " + id + " not found")
        );
    }
}
