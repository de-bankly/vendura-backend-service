package com.bankly.vendura.payment.giftcard.transaction.model;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@Document(collection = "giftcard_transactions")
@NoArgsConstructor
@AllArgsConstructor
public class GiftCardTransaction {

  @Id private String id;

  @DBRef GiftCard giftCard;
  private double amount;
  @DBRef private GiftcardTransactable transactionCause;
  @DBRef private User issuer;
  private String message;
}
