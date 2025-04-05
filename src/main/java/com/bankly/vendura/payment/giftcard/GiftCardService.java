package com.bankly.vendura.payment.giftcard;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.model.GiftCardDTO;
import com.bankly.vendura.payment.giftcard.model.GiftCardFactory;
import com.bankly.vendura.payment.giftcard.model.GiftCardRepository;
import com.bankly.vendura.payment.giftcard.transaction.GiftCardTransactionService;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GiftCardService {

  private final GiftCardRepository giftCardRepository;
  private final GiftCardTransactionService giftCardTransactionService;
  private final UserRepository userRepository;

  public GiftCard create(GiftCardDTO giftCardDTO, Authentication authentication) {
    if (giftCardDTO.getInitialBalance() == null || giftCardDTO.getInitialBalance() <= 0) {
      throw new EntityCreationException(
          "Initial balance must be greater than zero",
          HttpStatus.UNPROCESSABLE_ENTITY,
          "initialBalance",
          true);
    }
    if (giftCardDTO.getExpirationDate() != null
        && (giftCardDTO.getExpirationDate().after(new java.util.Date()))) {
      throw new EntityCreationException(
          "Expiration date must be in the future",
          HttpStatus.UNPROCESSABLE_ENTITY,
          "expirationDate",
          true);
    }
    giftCardDTO.setIssueDate(new Date());
    if (giftCardDTO.getExpirationDate() != null
            && giftCardDTO.getIssueDate().before(giftCardDTO.getExpirationDate())) {
      throw new EntityCreationException(
          "Issue date must be set before expiration date",
          HttpStatus.UNPROCESSABLE_ENTITY,
          "issueDate",
          true);
    }

    User user = this.userRepository.findUserByUsername(authentication.getName()).orElseThrow();

    GiftCard giftCard = GiftCardFactory.toEntity(giftCardDTO);
    giftCard.setIssuer(user);
    giftCard.setId(this.generateGiftCardId());
    giftCard = this.giftCardRepository.save(giftCard);

    this.giftCardTransactionService.createTransaction(
        giftCard, giftCardDTO.getInitialBalance(), null, user, "Gift card created");

    return giftCard;
  }

  private String generateGiftCardId() {
    String id;
    do {
      id = String.format("%016d", new java.util.Random().nextLong() & Long.MAX_VALUE);
    } while (giftCardRepository.existsById(id));
    return id;
  }

  public GiftCard update(String id, GiftCardDTO giftCardDTO) {
    return null;
  }

  public GiftCard delete(String id, Authentication authentication) {
    GiftCard giftCard = this.giftCardRepository.findById(id).orElseThrow();
    double remainingBalance = this.giftCardTransactionService.calculateRemainingBalance(giftCard);
    if (remainingBalance == 0) {
      return giftCard;
    }
    User user = this.userRepository.findUserByUsername(authentication.getName()).orElseThrow();
    this.giftCardTransactionService.createTransaction(
        giftCard, -remainingBalance, null, user, "Gift card deleted");

    return giftCard;
  }
}
