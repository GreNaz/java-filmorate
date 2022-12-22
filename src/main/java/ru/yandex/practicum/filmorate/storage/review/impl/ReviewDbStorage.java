package ru.yandex.practicum.filmorate.storage.review.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ObjectNotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.util.mapper.Mapper;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Repository
@Slf4j
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewStorage {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Review create(Review review) {

        String create = "INSERT " +
                "INTO REVIEW (CONTENT, IS_POSITIVE, USER_ID, FILM_ID, USEFUL) " +
                "VALUES ( ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(create, new String[]{"review_id"});
            stmt.setString(1, review.getContent());
            stmt.setBoolean(2, review.getIsPositive());
            stmt.setLong(3, review.getUserId());
            stmt.setLong(4, review.getFilmId());
            stmt.setInt(5, 0);
            return stmt;
        }, keyHolder);
        review.setReviewId(Objects.requireNonNull(keyHolder.getKey()).longValue());

        return review;
    }

    @Override
    public Review update(Review review) {
        String update = "UPDATE " +
                "REVIEW " +
                "SET CONTENT = ?, " +
                "IS_POSITIVE = ? " +
                "WHERE REVIEW_ID = ?";

        int updateResult = jdbcTemplate.update(update,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());

        if (updateResult == 0) {
            throw new ObjectNotFoundException("Review " + review + " was not found");
        }
        return review;
    }

    @Override
    public void removeReview(Long id) {
        String delete = "DELETE " +
                "FROM REVIEW " +
                "WHERE REVIEW_ID = ?";
        int updateResult = jdbcTemplate.update(delete, id);

        if (updateResult == 0) {
            throw new ObjectNotFoundException("Review with id " + id + " was not found and can not removed");
        }
    }

    @Override
    public Optional<Review> get(Long id) {
        String get = "SELECT * " +
                "FROM REVIEW " +
                "WHERE REVIEW_ID = ?";

        SqlRowSet reviewSet = jdbcTemplate.queryForRowSet(get, id);

        if (!reviewSet.next()) {
            return Optional.empty();
        }

        return Optional.ofNullable(jdbcTemplate.queryForObject(get, Mapper::reviewMapper, id));
    }

    @Override
    public List<Review> getReviews(Long filmId, int count) {
        String get = "SELECT * " +
                "FROM REVIEW " +
                "WHERE FILM_ID = ?" +
                "ORDER BY USEFUL DESC LIMIT ?";

        return jdbcTemplate.query(get, Mapper::reviewMapper, filmId, count);
    }

    @Override
    public List<Review> getReviews() {
        String sql = "SELECT * " +
                "FROM REVIEW " +
                "ORDER BY USEFUL DESC";
        return jdbcTemplate.query(sql, Mapper::reviewMapper);
    }

    @Override
    public void creatFeedback(Long id, Long userId, boolean isLike) {
        String sql = "MERGE INTO " +
                "REVIEW_LIKES ( REVIEW_ID, USER_ID, IS_LIKE ) " +
                "VALUES  ( ?, ?, ? )";

        int resultUpdate = jdbcTemplate.update(sql, id, userId, isLike);

        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Not found review or user");
        }

        if (isLike) {
            incrementUseful(id);
            log.info("Success add positive feedback from user with id {} to review with id {}", userId, id);
        } else {
            decrementUseful(id);
            log.info("Success add negative feedback from user with id {} to review with id {}", userId, id);
        }
    }

    @Override
    public void removeFeedback(Long id, Long userId, boolean isLike) {

        String sql = "DELETE FROM " +
                "REVIEW_LIKES " +
                "WHERE REVIEW_ID = ? " +
                "AND USER_ID = ? " +
                "AND IS_LIKE = ?";

        int resultUpdate = jdbcTemplate.update(sql, id, userId, isLike);

        if (resultUpdate == 0) {
            throw new ObjectNotFoundException("Not found review or user");
        }

        if (!isLike) {
            incrementUseful(id);
            log.info("Success delete negative feedback from user with id {} to review with id {}", userId, id);
        } else {
            decrementUseful(id);
            log.info("Success delete positive feedback from user with id {} to review with id {}", userId, id);
        }
    }

    private void incrementUseful(Long id) {
        String increment = "UPDATE REVIEW " +
                "SET USEFUL = USEFUL + 1 " +
                "WHERE REVIEW_ID = ?";

        int updateResult = jdbcTemplate.update(increment, id);

        if (updateResult == 0) {
            throw new ObjectNotFoundException("Error in process adding useful to review");
        }
    }

    private void decrementUseful(Long id) {
        String decrement = "UPDATE REVIEW " +
                "SET USEFUL = USEFUL - 1 " +
                "WHERE REVIEW_ID = ?";

        int updateResult = jdbcTemplate.update(decrement, id);

        if (updateResult == 0) {
            throw new ObjectNotFoundException("Error in process deleting useful from review");
        }
    }
}