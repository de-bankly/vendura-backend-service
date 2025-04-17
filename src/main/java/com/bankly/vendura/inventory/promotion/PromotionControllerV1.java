package com.bankly.vendura.inventory.promotion;

import com.bankly.vendura.inventory.promotion.model.PromotionDTO;
import com.bankly.vendura.inventory.promotion.model.PromotionFactory;
import com.bankly.vendura.utilities.ValidationGroup;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/promotion")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PromotionControllerV1 {

  private final PromotionService promotionService;
  private final PromotionFactory promotionFactory;

  @GetMapping
  public ResponseEntity<Page<PromotionDTO>> getAllPromotions(Pageable pageable) {
    return ResponseEntity.ok(
        this.promotionService.getAllPromotions(pageable).map(promotionFactory::toDTO));
  }

  @GetMapping("/{id}")
  public ResponseEntity<PromotionDTO> getPromotionById(@PathVariable("id") String id) {
    return ResponseEntity.ok(promotionFactory.toDTO(this.promotionService.getPromotionById(id)));
  }

  @PostMapping
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<PromotionDTO> createPromotion(
      @RequestBody @Validated(ValidationGroup.Create.class) PromotionDTO promotionDTO) {
    return ResponseEntity.ok(
        promotionFactory.toDTO(this.promotionService.createPromotion(promotionDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER')")
  public ResponseEntity<PromotionDTO> updatePromotion(
      @PathVariable("id") String id,
      @RequestBody @Validated(ValidationGroup.Update.class) PromotionDTO promotionDTO) {
    return ResponseEntity.ok(
        promotionFactory.toDTO(this.promotionService.updatePromotion(id, promotionDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Void> deletePromotion(@PathVariable("id") String id) {
        this.promotionService.deletePromotion(id);
        return ResponseEntity.noContent().build();
    }

}
