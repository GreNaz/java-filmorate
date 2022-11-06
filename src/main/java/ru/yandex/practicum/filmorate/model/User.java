package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Data
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class User {

    private long id;

    @NotNull
    @Email(message = "Некорректный адрес электронной почты")
    @EqualsAndHashCode.Include
    private final String email;

    @NotBlank(message = "Полученный логин пустой")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private final String login;

    private String name;

    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Полученная дата рождения еще не наступила")
    private final LocalDate birthday;
}
