package com.bankly.vendura.inventory.promotion.model;

import com.bankly.vendura.inventory.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PromotionFactory {

  private final ProductService productService;

  public PromotionDTO toDTO(Promotion promotion) {
    return PromotionDTO.builder()
        .id(promotion.getId())
        .productId(promotion.getProduct().getId())
        .begin(promotion.getBegin())
        .end(promotion.getEnd())
        .discount(promotion.getDiscount())
        .active(promotion.isActive())
        .build();
  }

  public Promotion toEntity(PromotionDTO promotionDTO) {
    return Promotion.builder()
        .id(promotionDTO.getId())
        .product(productService.findById(promotionDTO.getProductId()))
        .begin(promotionDTO.getBegin())
        .end(promotionDTO.getEnd())
        .discount(promotionDTO.getDiscount() != null ? promotionDTO.getDiscount() : 0)
        .build();
  }
}
