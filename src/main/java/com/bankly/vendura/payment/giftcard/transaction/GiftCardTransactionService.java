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

  public void createTransaction(
      GiftCard giftCard,
      double amount,
      GiftcardTransactable transactionCause,
      User issuer,
      String message) {
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
}
