package com.bankly.vendura.payment.giftcard;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.payment.giftcard.transaction.GiftCardTransactionService;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.model.GiftCardDTO;
import com.bankly.vendura.payment.giftcard.model.GiftCardFactory;
import com.bankly.vendura.utilities.ValidationGroup;
import com.bankly.vendura.payment.giftcard.model.GiftCardRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/giftcard")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class GiftCardControllerV1 {

  private final GiftCardService giftCardService;

  @GetMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<Page<GiftCardDTO>> getAllGiftCards(Pageable pageable) {
    return ResponseEntity.ok(
        this.giftCardService.findAllPageable(pageable).map(GiftCardFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<GiftCardDTO> getGiftCardById(@PathVariable String id) {
    return ResponseEntity.ok(GiftCardFactory.toDTO(this.giftCardService.findById(id)));
  }

  @GetMapping("/{id}/transactional")
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<GiftCardDTO> getGiftCardTransactionalInformation(@PathVariable String id) {
    return ResponseEntity.ok(this.giftCardService.getTransactionalInformationById(id));
  }

  @PostMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<GiftCardDTO> createGiftCard(
      @RequestBody @Validated(ValidationGroup.Create.class) GiftCardDTO giftCardDTO, @AuthenticationPrincipal User user) {
    LoggerFactory.getLogger(GiftCardControllerV1.class)
        .error(user == null ? "USER IS NULL" : user.toString());
    return ResponseEntity.ok(
        GiftCardFactory.toDTO(this.giftCardService.createFromDTOAuthenticated(giftCardDTO, user)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<GiftCardDTO> updateGiftCard(
      @PathVariable String id,
      @RequestBody @Validated(ValidationGroup.Update.class) GiftCardDTO giftCardDTO) {
    return ResponseEntity.ok(GiftCardFactory.toDTO(this.giftCardService.update(id, giftCardDTO)));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<GiftCardDTO> deleteGiftCard(
      @PathVariable String id, Authentication authentication) {
    GiftCard giftCard = this.giftCardService.delete(id, authentication);
    return ResponseEntity.ok(GiftCardFactory.toDTO(giftCard));
  }
}
