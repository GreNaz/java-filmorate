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

    private int id;

    @NotNull
    @Email(message = "Некорректный адрес электронной почты")
    @NotBlank(message = "Электронная почта не может быть пустой")
    @EqualsAndHashCode.Include
    private final String email;

    @NotNull(message = "Не указан логин")
    @NotBlank(message = "Полученный логин пустой")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private final String login;

    private String name;

    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Полученная дата рождения еще не наступила")
    private final LocalDate birthday;
}
