package com.bankly.vendura.payment.giftcard.model;

import com.bankly.vendura.authentication.user.model.User;

public class GiftCardFactory {

  public static GiftCard toEntity(GiftCardDTO giftCardDTO) {
    return new GiftCard(
        null,
        giftCardDTO.getIssueDate(),
        giftCardDTO.getExpirationDate(),
        giftCardDTO.getInitialBalance(),
        giftCardDTO.getIssuerId() != null ? new User("") : null);
  }

  public static GiftCardDTO toDTO(GiftCard giftCard) {
    return new GiftCardDTO(
        giftCard.getId(),
        giftCard.getIssueDate(),
        giftCard.getExpirationDate(),
        giftCard.getInitialBalance(),
        giftCard.getIssuer() != null ? giftCard.getIssuer().getId() : null,
        null);
  }
}
