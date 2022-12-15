package ru.yandex.practicum.filmorate.model;

import lombok.*;

import javax.validation.constraints.*;
import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class User {

    private long id;

    @NotNull
    @Email(message = "Incorrect email address")
    @EqualsAndHashCode.Include
    private String email;

    @NotBlank(message = "Received login is empty")
    @Pattern(regexp = "\\S+", message = "Login contains spaces")
    private String login;

    private String name;

    @NotNull(message = "Date of birth not specified")
    @PastOrPresent(message = "Received date of birth has not yet arrived")
    private LocalDate birthday;

}
