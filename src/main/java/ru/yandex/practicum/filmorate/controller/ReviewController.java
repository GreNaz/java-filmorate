package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.validation.Create;
import ru.yandex.practicum.filmorate.controller.validation.Update;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.constraints.Positive;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review create(@Validated(Create.class)
                         @RequestBody Review review) {

        log.info("Received a request to add a new {}", review);

        log.info("Starting creating review");
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Validated(Update.class)
                         @RequestBody Review review) {
        log.info("Received a request to update review with an id {}", review.getReviewId());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        log.info("Received a request to remove review with an id: {}", id);
        reviewService.delete(id);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable Long id) {
        log.info("Received a request to get review with an id: {}", id);
        return reviewService.get(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(defaultValue = "10") @Positive int count,
                                   @RequestParam(required = false) Long filmId) {
        log.info("Received a request to get list of {} popular reviews by film with an id = {}", count, filmId);
        return reviewService.getReviews(filmId, count);
    }

    @PutMapping("/{id}/{feedback}/{userId}")
    public Review creatFeedback(@PathVariable Long id,
                                @PathVariable Long userId,
                                @PathVariable String feedback) {
        log.info("Received a request to add feedback to review with an id: {}, from user with an id = {}", id, userId);
        return reviewService.creatFeedback(id, userId, feedback);
    }

    @DeleteMapping("/{id}/{feedback}/{userId}")
    public Review removeFeedback(@PathVariable Long id,
                                 @PathVariable Long userId,
                                 @PathVariable String feedback) {
        log.info("Received a request to remove feedback to review with an id: {}, from user with an id = {}", id, userId);
        return reviewService.removeFeedback(id, userId, feedback);
    }

}
