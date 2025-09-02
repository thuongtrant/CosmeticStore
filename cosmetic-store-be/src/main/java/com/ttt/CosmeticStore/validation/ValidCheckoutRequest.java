package com.ttt.CosmeticStore.validation;

import com.ttt.CosmeticStore.validation.impl.CheckoutRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CheckoutRequestValidator.class)
public @interface ValidCheckoutRequest {
    String message() default "Yêu cầu checkout không hợp lệ";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
