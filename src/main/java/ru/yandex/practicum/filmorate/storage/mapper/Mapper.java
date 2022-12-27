package ru.yandex.practicum.filmorate.storage.mapper;

import lombok.experimental.UtilityClass;
import ru.yandex.practicum.filmorate.model.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@UtilityClass
public class Mapper {
    public static Film filmMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("film_id");
        String name = resultSet.getString("name");
        String description = resultSet.getString("description");
        LocalDate releaseDate = resultSet.getDate("release_date").toLocalDate();
        int duration = resultSet.getInt("duration");
        Double rate = resultSet.getDouble("rate");
        Mpa mpa = new Mpa(resultSet.getInt("mpa.mpa_id"),
                resultSet.getString("mpa.name"));
        return new Film(id, name, description, releaseDate, duration, rate, mpa, new LinkedHashSet<>(),
                new LinkedHashSet<>());
    }

    public static Director directorMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("director_id");
        String name = resultSet.getString("name");
        return new Director(id, name);
    }

    public static Genre genreMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("genre_id");
        String name = resultSet.getString("name");
        return new Genre(id, name);
    }

    public static Mpa mpaMapper(ResultSet resultSet, int row) throws SQLException {
        int id = resultSet.getInt("mpa_id");
        String name = resultSet.getString("name");
        return new Mpa(id, name);
    }

    public static User userMapper(ResultSet resultSet, int row) throws SQLException {
        long id = resultSet.getLong("user_id");
        String email = resultSet.getString("email");
        String login = resultSet.getString("login");
        String name = resultSet.getString("name");
        LocalDate birthday = resultSet.getDate("birthday").toLocalDate();
        return new User(id, email, login, name, birthday);
    }

    public static Review reviewMapper(ResultSet resultSet, int row) throws SQLException {
        long reviewId = resultSet.getLong("review_id");
        String content = resultSet.getString("content");
        boolean isPositive = resultSet.getBoolean("is_positive");
        long userId = resultSet.getLong("user_id");
        long filmId = resultSet.getLong("film_id");
        int useful = resultSet.getInt("useful");

        return new Review(reviewId, content, isPositive, userId, filmId, useful);
    }
}
