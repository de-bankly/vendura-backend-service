package com.bankly.vendura.payment.giftcard.model;

import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardDTO {

  private String id;
  private Date issueDate;
  private Date expirationDate;
  private Double initialBalance;
  private String issuerId;

  private Double remainingBalance;

  private GiftCardDTO(Builder builder) {
    this.id = builder.id;
    this.issueDate = builder.issueDate;
    this.expirationDate = builder.expirationDate;
    this.initialBalance = builder.initialBalance;
    this.issuerId = builder.issuerId;
    this.remainingBalance = builder.remainingBalance;
  }


  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private String id;
    private Date issueDate;
    private Date expirationDate;
    private Double initialBalance;
    private String issuerId;
    private Double remainingBalance;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder issueDate(Date issueDate) {
      this.issueDate = issueDate;
      return this;
    }

    public Builder expirationDate(Date expirationDate) {
      this.expirationDate = expirationDate;
      return this;
    }

    public Builder initialBalance(Double initialBalance) {
      this.initialBalance = initialBalance;
      return this;
    }

    public Builder issuerId(String issuerId) {
      this.issuerId = issuerId;
      return this;
    }

    public Builder remainingBalance(Double remainingBalance) {
      this.remainingBalance = remainingBalance;
      return this;
    }

    public GiftCardDTO build() {
      return new GiftCardDTO(this);
    }
  }
}
