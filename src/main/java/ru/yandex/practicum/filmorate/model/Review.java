package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.controller.validation.Create;
import ru.yandex.practicum.filmorate.controller.validation.Update;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Review {
    @EqualsAndHashCode.Include
    @NotNull(groups = Update.class)
    private Long reviewId;
    @NotNull(groups = Create.class, message = "Received a review with an empty content")
    @Size(min = 1, max = 500, message = "Review content cannot be more than 500 characters")
    private String content;
    @NotNull(message = "Review grade can not be null. Valid values: true/false")
    private Boolean isPositive;
    @NotNull(groups = Create.class, message = "Review value userId can not be null")
    private Long userId;
    @NotNull(groups = Create.class, message = "Review value filmId can not be null")
    private Long filmId;
    private int useful;
}
