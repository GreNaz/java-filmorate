package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.Update;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/directors")
public class DirectorController {
    private final DirectorService directorService;

    @PostMapping
    public Director create(@Valid
                           @RequestBody Director director) {
        log.info("Received a request to crete directors");
        return directorService.create(director);
    }

    @PutMapping
    public Director update(@Validated(Update.class) @RequestBody Director director) {
        log.info("Received a request to update directors");
        return directorService.update(director);
    }

    @GetMapping
    public List<Director> get() {
        log.info("Received a request to get directors");
        return directorService.get();
    }

    @GetMapping("/{id}")
    public Director get(@PathVariable int id) {
        log.info("Received a request to get director with id = " + id);
        return directorService.get(id);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Received a request to delete director with id = " + id);
        directorService.delete(id);
    }
}
