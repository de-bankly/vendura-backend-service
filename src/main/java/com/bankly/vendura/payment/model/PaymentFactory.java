package com.bankly.vendura.payment.model;

import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.payment.giftcard.model.GiftCardRepository;

public class PaymentFactory {

  public static PaymentDTO toDTO(Payment payment) {
    PaymentDTO.PaymentDTOBuilder builder = PaymentDTO.builder();

    builder
        .id(payment.getId())
        .timestamp(payment.getTimestamp())
        .amount(payment.getAmount())
        .issuerId(payment.getIssuer().getId())
        .status(payment.getStatus().toDTOStatus());

    if (payment instanceof CardPayment) {
      CardPayment cardPayment = (CardPayment) payment;

      builder
          .cardNumber(cardPayment.getCardNumber())
          .cardHolderName(cardPayment.getCardHolderName())
          .expirationDate(cardPayment.getExpirationDate())
          .cvv(cardPayment.getCvv())
          .type(PaymentDTO.Type.CARD);
    }

    if (payment instanceof CashPayment) {
      CashPayment cashPayment = (CashPayment) payment;

      builder.handed(cashPayment.getHanded()).returned(cashPayment.getReturned());
    }

    if (payment instanceof GiftCardPayment) {
      GiftCardPayment giftCardPayment = (GiftCardPayment) payment;

      builder.giftcardId(giftCardPayment.getGiftCard().getId());
    }

    return builder.build();
  }

  public static Payment toEntity(
      PaymentDTO paymentDTO, UserRepository userRepository, GiftCardRepository giftCardRepository) {
    Payment payment = createEmptyPOJO(paymentDTO);

    payment.setId(paymentDTO.getId());
    payment.setTimestamp(paymentDTO.getTimestamp());
    payment.setAmount(paymentDTO.getAmount());
    payment.setIssuer(userRepository.findById(paymentDTO.getIssuerId()).orElse(null));
    payment.setStatus(paymentDTO.getStatus().toEntityStatus());

    if (payment instanceof CardPayment) {
      CardPayment cardPayment = (CardPayment) payment;

      cardPayment.setCardNumber(paymentDTO.getCardNumber());
      cardPayment.setCardHolderName(paymentDTO.getCardHolderName());
      cardPayment.setExpirationDate(paymentDTO.getExpirationDate());
      cardPayment.setCvv(paymentDTO.getCvv());
    }

    if (payment instanceof CashPayment) {
      CashPayment cashPayment = (CashPayment) payment;

      cashPayment.setHanded(paymentDTO.getHanded());
      cashPayment.setReturned(paymentDTO.getReturned());
    }

    if (payment instanceof GiftCardPayment) {
      GiftCardPayment giftCardPayment = (GiftCardPayment) payment;

      giftCardPayment.setGiftCard(
          giftCardRepository.findById(paymentDTO.getGiftcardId()).orElse(null));
    }

    return payment;
  }

  private static Payment createEmptyPOJO(PaymentDTO paymentDTO) {
    switch (paymentDTO.getType()) {
      case CASH:
        return new CashPayment();
      case CARD:
        return new CardPayment();
      case GIFTCARD:
        return new GiftCardPayment();
      default:
        throw new IllegalArgumentException("Unknown payment type: " + paymentDTO.getType());
    }
  }
}
