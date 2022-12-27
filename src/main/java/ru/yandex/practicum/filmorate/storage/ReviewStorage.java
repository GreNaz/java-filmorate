package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(Long id);

    List<Review> get();

    Optional<Review> get(Long id);

    List<Review> get(Long filmId, int count);

    void creatFeedback(Long id, Long userId, int isLike);

    void removeFeedback(Long id, Long userId, int isLike);
}
