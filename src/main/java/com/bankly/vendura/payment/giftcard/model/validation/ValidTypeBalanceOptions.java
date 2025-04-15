package com.bankly.vendura.payment.giftcard.model.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TypeBalanceOptionsValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTypeBalanceOptions {

  String message() default
      "Incompatible type and balance options: initialBalance must be set and greater than zero for type GIFT_CARD and discountPercentage and maximumUsages must be set for type DISCOUNT_CARD";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
