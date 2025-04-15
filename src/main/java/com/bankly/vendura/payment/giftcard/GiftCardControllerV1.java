package com.bankly.vendura.payment.giftcard;

import com.bankly.vendura.payment.giftcard.transaction.GiftCardTransactionService;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.model.GiftCardDTO;
import com.bankly.vendura.payment.giftcard.model.GiftCardFactory;
import com.bankly.vendura.payment.giftcard.model.GiftCardRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/giftcard")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GiftCardControllerV1 {

  private final GiftCardService giftCardService;
  private final GiftCardTransactionService giftCardTransactionService;
  private final GiftCardRepository giftCardRepository;

  @GetMapping("/{id}/balance")
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<GiftCardDTO> getGiftCardBalance(@PathVariable String id) {
    double balance = giftCardTransactionService.calculateRemainingBalance(id);
    return ResponseEntity.ok(GiftCardDTO.builder().id(id).remainingBalance(balance).build());
  }

  @GetMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<Page<GiftCardDTO>> getAllGiftCards(Pageable pageable) {
    return ResponseEntity.ok(this.giftCardRepository.findAll(pageable).map(GiftCardFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<GiftCardDTO> getGiftCardById(@PathVariable String id) {
    return ResponseEntity.ok(
        GiftCardFactory.toDTO(
            this.giftCardRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "GiftCard not found", HttpStatus.NOT_FOUND, id))));
  }

  @PostMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<GiftCardDTO> createGiftCard(
      @RequestBody GiftCardDTO giftCardDTO, Authentication authentication) {
    return ResponseEntity.ok(
        GiftCardFactory.toDTO(this.giftCardService.create(giftCardDTO, authentication)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<GiftCardDTO> updateGiftCard(
      @PathVariable String id, @RequestBody GiftCardDTO giftCardDTO) {
    return ResponseEntity.ok(GiftCardFactory.toDTO(this.giftCardService.update(id, giftCardDTO)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GiftCardDTO> deleteGiftCard(
      @PathVariable String id, Authentication authentication) {
    GiftCard giftCard = this.giftCardService.delete(id, authentication);
    return ResponseEntity.ok(GiftCardFactory.toDTO(giftCard));
  }
}
