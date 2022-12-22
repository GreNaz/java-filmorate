package ru.yandex.practicum.filmorate.storage.director;

import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;

public interface DirectorStorage {
    Optional<Director> create(Director director);
    Director update(Director director);
    void delete(int id);
    List<Director> getDirectors();
    Optional<Director> get(int id);

    void loadDirectors(List<Film> films);
}
