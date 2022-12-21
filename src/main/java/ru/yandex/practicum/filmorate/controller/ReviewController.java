package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;

import javax.validation.Valid;
import javax.validation.ValidationException;
import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    private final UserService userService;
    private final FilmService filmService;

    @PostMapping
    public Review create(@Validated @RequestBody Review review) {

        log.info("Received a request to add a new " + review);

        //4 провери для 2  тестов в постмане
        //из-за которых я не могу просто нормально валидировать данные
        //база данных не даст положить неконсистентные значения
        //а на уровне модели можно просто ограничить значения <= 0
        //нагенерировано 2 лишних запроса в бд - в интерпрайсе было бы больно
        //вынес на этот уровень чтобы точно увидели

        if (review.getUserId() == 0) {
            throw new ValidationException("Incorrect user id");
        }

        if (review.getFilmId() == 0) {
            throw new ValidationException("Incorrect film id");
        }
        log.info("Validating input user id... ");
        userService.get(review.getUserId());
        log.info("Validating input film id... ");
        filmService.get(review.getFilmId());

        log.info("Starting creating review");
        return reviewService.create(review);
    }

    @PutMapping
    public Review update(@Valid @RequestBody Review review) {
        log.info("Received a request to update review with an id {}", review.getReviewId());
        return reviewService.update(review);
    }

    @DeleteMapping("/{id}")
    public void removeReview(@PathVariable Long id) {
        log.info("Received a request to remove review with an id: {}", id);
        reviewService.removeReview(id);
    }

    @GetMapping("/{id}")
    public Review get(@PathVariable Long id) {
        log.info("Received a request to get review with an id: {}", id);
        return reviewService.get(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam(required = false, defaultValue = "10") int count,
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
