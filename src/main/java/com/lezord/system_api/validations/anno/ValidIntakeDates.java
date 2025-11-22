package com.lezord.system_api.validations.anno;

import com.lezord.system_api.validations.IntakeDateValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = IntakeDateValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidIntakeDates {
    String message() default "intakeEndDate must be after intakeStartDate";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
