//package ru.yandex.practicum.filmorate.storage.recommendation;
//
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import lombok.Data;
//import lombok.RequiredArgsConstructor;
//import ru.yandex.practicum.filmorate.model.Film;
//import ru.yandex.practicum.filmorate.model.User;
//import ru.yandex.practicum.filmorate.storage.FilmStorage;
//
//@Data
//@RequiredArgsConstructor
//public class InputData {
//    private final FilmStorage filmStorage;
//    public List<Film> items = filmStorage.get();
//
//    public Map<User, HashMap<Film, Double>> initializeData(int numberOfUsers) {
//        Map<User, HashMap<Film, Double>> data = new HashMap<>();
//
//        HashMap<Film, Double> newUser;
//        Set<Film> newRecommendationSet;
//
//        for (int i = 0; i < numberOfUsers; i++) {
//            newUser = new HashMap<>();
//            newRecommendationSet = new HashSet<>();
//
//            for (int j = 0; j < 3; j++) {
//                newRecommendationSet.add(items.get((int) (Math.random() * 5)));
//            }
//
//            for (Film item : newRecommendationSet) {
//                newUser.put(item, Math.random());
//            }
//
//            data.put(new User("User " + i), newUser);
//        }
//        return data;
//    }
//
//}