package com.bankly.vendura.payment.giftcard.transaction;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.transaction.model.GiftCardTransaction;
import com.bankly.vendura.payment.giftcard.transaction.model.GiftCardTransactionRepository;
import com.bankly.vendura.payment.giftcard.transaction.model.GiftcardTransactable;
import com.mongodb.DBRef;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GiftCardTransactionService {

  private final MongoTemplate mongoTemplate;
  private final GiftCardTransactionRepository giftCardTransactionRepository;

  public double calculateRemainingBalance(GiftCard giftCard) {
    return this.calculateRemainingBalance(giftCard.getId());
  }

  public double calculateRemainingBalance(String giftCardId) {
    Aggregation aggregation =
        Aggregation.newAggregation(
            Aggregation.match(Criteria.where("giftCard").is(new DBRef("giftcards", giftCardId))),
            Aggregation.group("giftCard._id").sum("amount").as("amount"));

    GiftCardTransaction result =
        this.mongoTemplate
            .aggregate(aggregation, GiftCardTransaction.class, GiftCardTransaction.class)
            .getUniqueMappedResult();

    return result == null ? 0 : result.getAmount();
  }

  public void createBalanceTransaction(
      GiftCard giftCard,
      double amount,
      GiftcardTransactable transactionCause,
      User issuer,
      String message) {
    if (giftCard.getType() != GiftCard.Type.GIFT_CARD) {
      throw new IllegalArgumentException("Only GIFT_CARD type is allowed for balance transactions");
    }
    if (amount <= 0) {
      throw new IllegalArgumentException("Amount must be greater than zero");
    }
    double currentBalance = this.calculateRemainingBalance(giftCard);
    if (currentBalance + amount < 0) {
      throw new IllegalArgumentException("Insufficient balance for this transaction");
    }

    GiftCardTransaction giftCardTransaction =
        GiftCardTransaction.builder()
            .giftCard(giftCard)
            .amount(amount)
            .transactionCause(transactionCause)
            .issuer(issuer)
            .message(message)
            .build();
    this.giftCardTransactionRepository.save(giftCardTransaction);
  }

  public Integer calculateRemainingUsages(GiftCard giftCard) {
    return this.calculateRemainingUsages(giftCard.getId());
  }

  private Integer calculateRemainingUsages(String id) {
    return null; // TODO
  }


}
