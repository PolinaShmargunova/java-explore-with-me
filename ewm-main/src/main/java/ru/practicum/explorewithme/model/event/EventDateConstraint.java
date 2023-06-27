package ru.practicum.explorewithme.model.event;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateConstraint {
    String message() default "Начало должны быть не ранее, чем через 2 часа от момента создания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
