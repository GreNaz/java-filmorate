package ru.yandex.practicum.filmorate.storage.recommendation;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Data;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.MarksStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

@Data
@Repository
public class InputData {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final MarksStorage marksStorage;

    protected static List<Film> items;

    public InputData(UserStorage userStorage, FilmStorage filmStorage, MarksStorage marksStorage) {
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.marksStorage = marksStorage;
        this.items = this.filmStorage.get();
    }

    public Map<User, HashMap<Film, Double>> initializeData() {
        Map<User, HashMap<Film, Double>> data = new HashMap<>();
        HashMap<Film, Double> filmsWithMarks = new HashMap<>();
//        userStorage.get().stream()
//                .forEach(user -> {
//                    marksStorage.getMarksByUser(user.getId()).stream()
//                            .forEach(film -> {
//                                filmsWithMarks.put(
//                                        film,
//                                        marksStorage.getMarkByUser(user.getId(), film.getId()));
//                            });
//                    data.put(user, filmsWithMarks);
//                });
        for (User user : userStorage.get()) {

            List<Film> markedFilms = marksStorage.getMarksByUser(user.getId());
            filmsWithMarks = new HashMap<>();
            for (Film markedFilm : markedFilms) {
                filmsWithMarks.put(markedFilm, markedFilm.getRate());
            }

            data.put(user, filmsWithMarks);
        }
        return data;
    }
}