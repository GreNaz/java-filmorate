package ru.yandex.practicum.filmorate.model;

import lombok.*;
import ru.yandex.practicum.filmorate.controller.validation.Update;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@EqualsAndHashCode

public class Director {
    @NotNull(groups = Update.class)
    private Integer id;

    @NotBlank
    private String name;

    public Map<String, ?> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        return values;
    }
}
