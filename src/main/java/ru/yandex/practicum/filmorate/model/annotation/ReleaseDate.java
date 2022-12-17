package ru.yandex.practicum.filmorate.model.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ReleaseDateValidator.class)
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ReleaseDate {

    String message() default "Filmmaking has been interrupted! The release date is earlier than the first film";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
