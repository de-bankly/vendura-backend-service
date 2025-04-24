package com.bankly.vendura.payment.giftcard.model;

import com.bankly.vendura.authentication.user.model.User;

public class GiftCardFactory {

  public static GiftCard toEntity(GiftCardDTO giftCardDTO) {
    return new GiftCard(
        null,
        giftCardDTO.getIssueDate(),
        giftCardDTO.getExpirationDate(),
        giftCardDTO.getInitialBalance() == null ? 0 : giftCardDTO.getInitialBalance(),
        giftCardDTO.getIssuerId() != null ? new User(giftCardDTO.getIssuerId()) : null,
        giftCardDTO.getDiscountPercentage() == null ? 0 : giftCardDTO.getDiscountPercentage(),
        giftCardDTO.getMaximumUsages() == null ? 0 : giftCardDTO.getMaximumUsages(),
        giftCardDTO.getType() != null ? giftCardDTO.getType().toEntityType() : null);
  }

  public static GiftCardDTO toDTO(GiftCard giftCard) {
    return new GiftCardDTO(
        giftCard.getId(),
        giftCard.getIssueDate(),
        giftCard.getExpirationDate(),
        giftCard.getInitialBalance(),
        giftCard.getIssuer() != null ? giftCard.getIssuer().getId() : null,
        giftCard.getDiscountPercentage() == 0 ? null : giftCard.getDiscountPercentage(),
        giftCard.getMaximumUsages() == 0 ? null : giftCard.getMaximumUsages(),
        giftCard.getType() != null ? giftCard.getType().toDTOType() : null,
        null, null);
  }
}
