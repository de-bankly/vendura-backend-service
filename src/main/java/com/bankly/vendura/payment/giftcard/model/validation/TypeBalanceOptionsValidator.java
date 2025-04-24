package com.bankly.vendura.payment.giftcard.model.validation;

import com.bankly.vendura.payment.giftcard.model.GiftCardDTO;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TypeBalanceOptionsValidator
    implements ConstraintValidator<ValidTypeBalanceOptions, GiftCardDTO> {
  @Override
  public boolean isValid(GiftCardDTO giftCardDTO, ConstraintValidatorContext context) {
    if (giftCardDTO.getType() == GiftCardDTO.Type.GIFT_CARD) {
      if (giftCardDTO.getInitialBalance() == null || giftCardDTO.getInitialBalance() <= 0) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "Initial balance must be set and greater than zero but can only be set on creation")
            .addPropertyNode("initialBalance")
            .addConstraintViolation();
        return false;
      }
    }
    if (giftCardDTO.getType() == GiftCardDTO.Type.DISCOUNT_CARD) {
      if (giftCardDTO.getDiscountPercentage() == null
          || giftCardDTO.getDiscountPercentage() <= 0
          || giftCardDTO.getDiscountPercentage() > 100) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "Discount percentage must be set and between 1 and 100")
            .addPropertyNode("discountPercentage")
            .addConstraintViolation();
        return false;
      }
      if (giftCardDTO.getMaximumUsages() == null || giftCardDTO.getMaximumUsages() <= 0) {
        context.disableDefaultConstraintViolation();
        context
            .buildConstraintViolationWithTemplate(
                "Maximum usages must be set and greater than zero")
            .addPropertyNode("maximumUsages")
            .addConstraintViolation();
        return false;
      }
    }
    return true;
  }
}
