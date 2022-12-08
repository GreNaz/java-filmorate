package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {

    private long id;

    private Set<Long> likes = new HashSet<>();

    @EqualsAndHashCode.Include
    @NotBlank(message = "Получен фильм с пустым названием")
    private final String name;

    @Size(min = 1, max = 200, message = "Описание фильма не может быть больше 200 символов")
    @NotNull(message = "Не указано описание фильма")
    private final String description;

    @EqualsAndHashCode.Include
    @NotNull
    private final LocalDate releaseDate;

    @Min(value = 1, message = "Получена отрицательная или равная 0 продолжительность фильма")
    private final int duration;

    public Set<Long> addLike(Long id) {
        likes.add(id);
        return likes;
    }
}
