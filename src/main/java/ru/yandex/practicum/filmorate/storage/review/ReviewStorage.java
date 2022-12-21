package ru.yandex.practicum.filmorate.storage.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;
import java.util.Optional;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void removeReview(Long id);

    List<Review> getReviews();

    Optional<Review> get(Long id);

    List<Review> getReviews(Long filmId, int count);

    void creatFeedback(Long id, Long userId, boolean isLike);

    void removeFeedback(Long id, Long userId, boolean isLike);
}
