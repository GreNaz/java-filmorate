package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Director create(Director director);
    Director update(Director director);
    void delete(int id);
    List<Director> getDirectors();
    Optional<Director> get(int id);
}
