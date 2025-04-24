package com.bankly.vendura.payment.giftcard;

import com.bankly.vendura.authentication.user.UserService;
import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.model.GiftCardDTO;
import com.bankly.vendura.payment.giftcard.model.GiftCardFactory;
import com.bankly.vendura.payment.giftcard.model.GiftCardRepository;
import com.bankly.vendura.payment.giftcard.transaction.GiftCardTransactionService;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GiftCardService {

  private final GiftCardRepository giftCardRepository;

  private final GiftCardTransactionService giftCardTransactionService;
  private final UserService userService;

  /**
   * Creates a new GiftCard from the given DTO.
   *
   * @param giftCardDTO information to create the GiftCard
   * @param user the authentication of the user creating the GiftCard or null if created by the
   *     system
   * @return
   */
  public GiftCard createFromDTOAuthenticated(GiftCardDTO giftCardDTO, User user) {
    giftCardDTO.setIssueDate(new Date());
    user =
        this.userService
            .findById(user.getId())
            .orElseThrow(
                () ->
                    new EntityCreationException(
                        "Issuer ID was not found as user ID",
                        HttpStatus.NOT_FOUND,
                        "GiftCard",
                        false));

    GiftCard giftCard = GiftCardFactory.toEntity(giftCardDTO);
    giftCard.setIssuer(user);
    giftCard.setId(this.generateGiftCardId());
    giftCard = this.giftCardRepository.save(giftCard);

    if (giftCard.getType() == GiftCard.Type.GIFT_CARD) {
      this.giftCardTransactionService.createTransaction(
          giftCard, giftCardDTO.getInitialBalance(), null, user, "Gift card created", null);
    }

    return giftCard;
  }

  private String generateGiftCardId() {
    String id;
    do {
      long randomValue = Math.abs(new java.util.Random().nextLong()) % 10000000000000000L;
      id = String.format("%016d", randomValue);
    } while (giftCardRepository.existsById(id));
    return id;
  }

  public GiftCard update(String id, GiftCardDTO giftCardDTO) {
    GiftCard giftCard =
        this.giftCardRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("GiftCard not found", HttpStatus.NOT_FOUND, id));

    if (giftCardDTO.getExpirationDate() != null) {
      giftCard.setExpirationDate(giftCardDTO.getExpirationDate());
    }

    if (giftCardDTO.getDiscountPercentage() != null) {
      giftCard.setDiscountPercentage(giftCardDTO.getDiscountPercentage());
    }

    if (giftCardDTO.getMaximumUsages() != null) {
      giftCard.setMaximumUsages(giftCardDTO.getMaximumUsages());
    }

    if (giftCard.getType() != null) {
      giftCard.setType(giftCardDTO.getType().toEntityType());
    }

    return this.giftCardRepository.save(giftCard);
  }

  public GiftCard delete(String id, Authentication authentication) {
    GiftCard giftCard = this.giftCardRepository.findById(id).orElseThrow();

    if (giftCard.getType() == GiftCard.Type.GIFT_CARD) {
      double remainingBalance = this.giftCardTransactionService.calculateRemainingBalance(giftCard);
      if (remainingBalance == 0) {
        return giftCard;
      }
      User user = this.userService.findByUsername(authentication.getName()).orElseThrow();
      this.giftCardTransactionService.createTransaction(
          giftCard, -remainingBalance, null, user, "Gift card deleted", null);
    }

    if (giftCard.getType() == GiftCard.Type.DISCOUNT_CARD) {
      int remainingUsages =
          giftCard.getMaximumUsages() - this.giftCardTransactionService.calculateUsages(giftCard);
      if (remainingUsages == 0) {
        return giftCard;
      }
      User user = this.userService.findByUsername(authentication.getName()).orElseThrow();
      this.giftCardTransactionService.createTransaction(
          giftCard, -remainingUsages, null, user, "Gift card deleted", remainingUsages);
    }

    return giftCard;
  }

  public GiftCard findById(String id) {
    return this.giftCardRepository
        .findById(id)
        .orElseThrow(
            () -> new EntityRetrieveException("GiftCard not found", HttpStatus.NOT_FOUND, id));
  }

  public Page<GiftCard> findAllPageable(Pageable pageable) {
    return this.giftCardRepository.findAll(pageable);
  }

  public Double calculateRemainingBalanceById(String id) {
    GiftCard giftCard =
        this.giftCardRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("GiftCard not found", HttpStatus.NOT_FOUND, id));
    if (giftCard.getType() != GiftCard.Type.GIFT_CARD) {
      throw new EntityRetrieveException(
          "GiftCard is from type DISCOUNT_CARD", HttpStatus.PRECONDITION_FAILED, id);
    }
    return this.giftCardTransactionService.calculateRemainingBalance(giftCard);
  }

  public GiftCardDTO getTransactionalInformationById(String id) {
    GiftCard giftCard =
        this.giftCardRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("GiftCard not found", HttpStatus.NOT_FOUND, id));

    GiftCardDTO giftCardDTO = GiftCardFactory.toDTO(giftCard);

    if (giftCard.getType() == GiftCard.Type.GIFT_CARD) {
      giftCardDTO.setRemainingBalance(
          this.giftCardTransactionService.calculateRemainingBalance(giftCard));
    }

    if (giftCard.getType() == GiftCard.Type.DISCOUNT_CARD) {
      Integer i =
          giftCard.getMaximumUsages() - this.giftCardTransactionService.calculateUsages(giftCard);
      giftCardDTO.setRemainingUsages(i);
    }

    return giftCardDTO;
  }
}
