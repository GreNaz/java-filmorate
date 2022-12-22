package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@AllArgsConstructor
public class Review {
    @EqualsAndHashCode.Include
    private long reviewId;
    @NotBlank(message = "Received a review with an empty content")
    @Size(min = 1, max = 500, message = "Review content cannot be more than 500 characters")
    private String content;
    @NotNull(message = "Review grade can not be null. Valid values: true/false")
    private Boolean isPositive;
    @NotNull(message = "Review value userId can not be null")
    private long userId;
    @NotNull(message = "Review value filmId can not be null")
    private long filmId;
    @NotNull(message = "Review value useful can not be null")
    private int useful;
}
