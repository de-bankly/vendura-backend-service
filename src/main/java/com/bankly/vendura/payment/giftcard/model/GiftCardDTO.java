package com.bankly.vendura.payment.giftcard.model;

import com.bankly.vendura.payment.giftcard.model.validation.ValidTypeBalanceOptions;
import com.bankly.vendura.utilities.ValidationGroup;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ValidTypeBalanceOptions(groups = {ValidationGroup.Create.class})
public class GiftCardDTO {

  @Null(message = "ID will be auto-generated on creation and cannot be updated", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
  private String id;

  @Null(message = "Issue date will be set while creation and cannot be defined externally", groups = {ValidationGroup.Create.class, ValidationGroup.Update.class})
  private Date issueDate;
  private Date expirationDate;
  @Null(groups = ValidationGroup.Update.class, message = "Initial balance cannot be updated")
  private Double initialBalance;
  @Null(groups = ValidationGroup.Update.class, message = "Issuer ID cannot be updated")
  private String issuerId;
  private Integer discountPercentage;
  private Integer maximumUsages;
  @NotNull(message = "Type cannot be null", groups = ValidationGroup.Create.class)
  private Type type;

  private Double remainingBalance;
  private Integer remainingUsages;

  private GiftCardDTO(Builder builder) {
    this.id = builder.id;
    this.issueDate = builder.issueDate;
    this.expirationDate = builder.expirationDate;
    this.initialBalance = builder.initialBalance;
    this.issuerId = builder.issuerId;
    this.remainingBalance = builder.remainingBalance;
    this.discountPercentage = builder.discountPercentage;
    this.type = builder.type;
    this.maximumUsages = builder.maximumUsages;
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
    private Integer discountPercentage;
    private Type type;
    private Integer maximumUsages;

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

    public Builder type(Type type) {
      this.type = type;
      return this;
    }

    public Builder discountPercentage(Integer discountPercentage) {
      this.discountPercentage = discountPercentage;
      return this;
    }

    public Builder maximumUsages(Integer maximumUsages) {
      this.maximumUsages = maximumUsages;
      return this;
    }


    public GiftCardDTO build() {
      return new GiftCardDTO(this);
    }
  }

  public enum Type {
    GIFT_CARD,
    DISCOUNT_CARD;

    public GiftCard.Type toEntityType() {
      return GiftCard.Type.valueOf(this.name());
    }
  }
}
