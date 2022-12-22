package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Optional<Director> creteDirectors(@Valid @RequestBody Director director) {
        log.info("Received a request to crete directors");
        return directorService.createDirector(director);
    }

    @PutMapping
    public Director updateDirectors(@Valid @RequestBody Director director) {
        log.info("Received a request to update directors");
        return directorService.updateDirector(director);
    }

    @GetMapping
    public List<Director> getDirectors() {
        log.info("Received a request to get directors");
        return directorService.getDirectors();
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        log.info("Received a request to get director with id = " + id);
        return directorService.get(id);
    }

    @DeleteMapping("/{id}")
    public void deleteDirector(@PathVariable int id) {
        log.info("Received a request to delete director with id = " + id);
        directorService.deleteDirector(id);
    }
}
