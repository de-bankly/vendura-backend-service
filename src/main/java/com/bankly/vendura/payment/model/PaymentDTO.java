package com.bankly.vendura.payment.model;

import java.util.Date;
import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {

  private String id;
  private Date timestamp;
  private double amount;
  private String issuerId;
  private Status status;

  private Type type;

  // Card Payment Fields

  private String cardNumber;
  private String cardHolderName;
  private String expirationDate;
  private String cvv;

  // Cash Payment Fields

  private double handed;
  private double returned;

  // Gift Card Payment Fields

  private String giftcardId;

  public enum Type {
    CASH,
    CARD,
    GIFTCARD;
  }

  public enum Status {
    PENDING,
    COMPLETED,
    FAILED,
    REVERTED;

    public Payment.Status toEntityStatus() {
      return Payment.Status.valueOf(this.name());
    }
  }
}
