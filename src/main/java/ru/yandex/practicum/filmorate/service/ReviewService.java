package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventOperation;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.event.EventStorage;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewStorage reviewStorage;
    private final EventStorage eventStorage;

    private static final boolean LIKE = true;
    private static final boolean DISLIKE = false;

    public Review create(Review review) {
        log.info("Create a {} ", review);
        Review reviewNew = reviewStorage.create(review);
        Event event = new Event(review.getUserId(), EventType.REVIEW, EventOperation.ADD, reviewNew.getReviewId());
        log.info("Added a 'Review'.");
        eventStorage.addEvent(event);
        return reviewNew;
    }

    public Review get(Long id) {
        log.info("Getting review id = {}", id);
        return reviewStorage.get(id).orElseThrow(
                () -> new ObjectNotFoundException("Review with an id = " + id + " was not found"));
    }

    public Review update(Review review) {
        log.info("Updating a review {}", review);
        reviewStorage.update(review);
        Review result = reviewStorage.get(review.getReviewId()).orElseThrow(
                () -> new ObjectNotFoundException("The review  was not found."));
        Event event = new Event(result.getUserId(), EventType.REVIEW, EventOperation.UPDATE, review.getReviewId());
        log.info("Updated a 'Review'.");
        eventStorage.addEvent(event);
        return result;
    }

    public void removeReview(Long id) {
        Review review = get(id);
        log.info("Removing a review with an id = {}", id);
        Event event = new Event(review.getUserId(), EventType.REVIEW, EventOperation.REMOVE, id);
        log.info("'Review' removed.");
        eventStorage.addEvent(event);
        reviewStorage.removeReview(id);
    }

    public List<Review> getReviews(Long filmId, int count) {
        if (filmId != null) {
            log.info("Getting list of {} popular reviews by film with an id = {}", filmId, count);
            return reviewStorage.getReviews(filmId, count);
        } else {
            log.info("Getting full list of reviews");
            return reviewStorage.getReviews();
        }
    }

    public Review creatFeedback(Long id, Long userId, String criterion) {
        if (criterion.equals("like")) {
            log.info("Adding a like to a review with an id = {} from a user with an id = {}", id, userId);
            reviewStorage.creatFeedback(id, userId, LIKE);
        } else if (criterion.equals("dislike")) {
            log.info("Adding a dislike to a review with an id = {} from a user with an id = {}", id, userId);
            reviewStorage.creatFeedback(id, userId, DISLIKE);
        } else {
            throw new ValidationException("Incorrect end-point, please check value like/dislike in url");
        }
        return get(id);
    }

    public Review removeFeedback(Long id, Long userId, String criterion) {
        if (criterion.equals("like")) {
            log.info("Removing a like from a review with an id = {} from a user with an id = {}", id, userId);
            reviewStorage.removeFeedback(id, userId, LIKE);
        } else if (criterion.equals("dislike")) {
            log.info("Removing a dislike from a review with an id = {} from a user with an id = {}", id, userId);
            reviewStorage.removeFeedback(id, userId, DISLIKE);
        } else {
            throw new ValidationException("Incorrect end-point, please check value like/dislike in url");
        }
        return get(id);
    }
}
