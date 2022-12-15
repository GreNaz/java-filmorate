package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.model.annotation.ReleaseDate;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.LinkedHashSet;

@Getter
@Setter
@Builder
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Film {

    @EqualsAndHashCode.Include
    private long id;

    @NotBlank(message = "Received a movie with an empty title")
    private String name;

    @Size(min = 1, max = 200, message = "Movie description cannot be more than 200 characters")
    @NotNull(message = "Movie description not provided")
    private String description;

    @NotNull
    @ReleaseDate
    private LocalDate releaseDate;

    @Min(value = 1, message = "Received negative or equal to 0 movie duration")
    private int duration;

    @NotNull
    private Mpa mpa;

    private LinkedHashSet<Genre> genres;
}
