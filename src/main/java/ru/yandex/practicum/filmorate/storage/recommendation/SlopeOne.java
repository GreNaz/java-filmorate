package ru.yandex.practicum.filmorate.storage.recommendation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Slope One algorithm implementation
 */
@Slf4j
@RequiredArgsConstructor
public class SlopeOne {
    private final FilmStorage filmStorage;
    private final InputData inp;
    private final Map<Film, Map<Film, Double>> diff = new HashMap<>();
    private final Map<Film, Map<Film, Integer>> freq = new HashMap<>();
    private final Map<User, HashMap<Film, Double>> outputData = new HashMap<>();


    public Map<User, HashMap<Film, Double>> slopeOne() {
        Map<User, HashMap<Film, Double>> inputData = inp.initializeData();
        buildDifferencesMatrix(inputData);
        predict(inputData);
        return outputData;
    }

    /**
     * Based on the available data, calculate the relationships between the
     * items and number of occurrences
     *
     * @param data existing user data and their items' ratings
     */
    private void buildDifferencesMatrix(Map<User, HashMap<Film, Double>> data) {
        for (HashMap<Film, Double> user : data.values()) {
            for (Entry<Film, Double> e : user.entrySet()) {
                if (!diff.containsKey(e.getKey())) {
                    diff.put(e.getKey(), new HashMap<>());
                    freq.put(e.getKey(), new HashMap<>());
                }
                for (Entry<Film, Double> e2 : user.entrySet()) {
                    int oldCount = 0;
                    if (freq.get(e.getKey()).containsKey(e2.getKey())) {
                        oldCount = freq.get(e.getKey()).get(e2.getKey());
                    }
                    double oldDiff = 0.0;
                    if (diff.get(e.getKey()).containsKey(e2.getKey())) {
                        oldDiff = diff.get(e.getKey()).get(e2.getKey());
                    }
                    double observedDiff = e.getValue() - e2.getValue();
                    freq.get(e.getKey()).put(e2.getKey(), oldCount + 1);
                    diff.get(e.getKey()).put(e2.getKey(), oldDiff + observedDiff);
                }
            }
        }
        for (Film j : diff.keySet()) {
            for (Film i : diff.get(j).keySet()) {
                double oldValue = diff.get(j).get(i);
                int count = freq.get(j).get(i);
                diff.get(j).put(i, oldValue / count);
            }
        }
    }

    /**
     * Based on existing data predict all missing ratings. If prediction is not
     * possible, the value will be equal to -1
     *
     * @param data existing user data and their items' ratings
     */
    private void predict(Map<User, HashMap<Film, Double>> data) {
        HashMap<Film, Double> uPred = new HashMap<>();
        HashMap<Film, Integer> uFreq = new HashMap<>();
        for (Film j : diff.keySet()) {
            uFreq.put(j, 0);
            uPred.put(j, 0.0);
        }
        for (Entry<User, HashMap<Film, Double>> e : data.entrySet()) {
            for (Film j : e.getValue().keySet()) {
                for (Film k : diff.keySet()) {
                    try {
                        double predictedValue = diff.get(k).get(j) + e.getValue().get(j);
                        double finalValue = predictedValue * freq.get(k).get(j);
                        uPred.put(k, uPred.get(k) + finalValue);
                        uFreq.put(k, uFreq.get(k) + freq.get(k).get(j));
                    } catch (NullPointerException exception) {
                        throw new ObjectNotFoundException("Error in process calculate recommendations");
                    }
                }
            }
            HashMap<Film, Double> clean = new HashMap<>();
            for (Film j : uPred.keySet()) {
                if (uFreq.get(j) > 0) {
                    clean.put(j, uPred.get(j) / uFreq.get(j));
                }
            }
            for (Film j : filmStorage.get()) {
                if (e.getValue().containsKey(j)) {
                    clean.put(j, e.getValue().get(j));
                } else if (!clean.containsKey(j)) {
                    clean.put(j, -1.0);
                }
            }
            outputData.put(e.getKey(), clean);
        }
    }
}