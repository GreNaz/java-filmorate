package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class User {

    private String name;

    private long id;

    private Set<Long> friends = new HashSet<>();

    @NotNull
    @Email(message = "Некорректный адрес электронной почты")
    @EqualsAndHashCode.Include
    private final String email;

    @NotBlank(message = "Полученный логин пустой")
    @Pattern(regexp = "\\S+", message = "Логин содержит пробелы")
    private final String login;

    @NotNull(message = "Не указана дата рождения")
    @PastOrPresent(message = "Полученная дата рождения еще не наступила")
    private final LocalDate birthday;

    public Set<Long> addFriend(Long id) {
        friends.add(id);
        return friends;
    }
}
