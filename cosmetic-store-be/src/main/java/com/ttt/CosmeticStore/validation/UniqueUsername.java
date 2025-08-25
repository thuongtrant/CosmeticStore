package com.ttt.CosmeticStore.validation;

import com.ttt.CosmeticStore.validation.impl.UniqueUsernameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueUsernameValidator.class)
@Documented
public @interface UniqueUsername {
    String message() default "Tên đăng nhập đã được sử dụng";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}