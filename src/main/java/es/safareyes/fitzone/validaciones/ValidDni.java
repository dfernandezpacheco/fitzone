package es.safareyes.fitzone.validaciones;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DniValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDni {
    String message() default "El DNI no tiene un formato correcto o la letra no se corresponde";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}