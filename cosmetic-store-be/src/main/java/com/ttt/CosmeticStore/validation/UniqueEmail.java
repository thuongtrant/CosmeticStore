package com.ttt.CosmeticStore.validation;

import com.ttt.CosmeticStore.validation.impl.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueEmailValidator.class)
@Documented
public @interface UniqueEmail {
    String message() default "Email đã được sử dụng";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}