package ru.practicum.explorewithme.model.event;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = EventDateValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EventDateConstraint {
    String message() default "Начало должны быть не ранее, чем через 2 часа от момента создания";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
