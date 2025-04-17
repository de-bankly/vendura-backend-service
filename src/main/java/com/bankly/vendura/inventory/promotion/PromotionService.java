package com.bankly.vendura.inventory.promotion;

import com.bankly.vendura.inventory.promotion.model.Promotion;
import com.bankly.vendura.inventory.promotion.model.PromotionDTO;
import com.bankly.vendura.inventory.promotion.model.PromotionFactory;
import com.bankly.vendura.inventory.promotion.model.PromotionRepository;
import com.bankly.vendura.sale.model.Sale;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import java.util.Comparator;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PromotionService {

  private static final Logger LOGGER = LoggerFactory.getLogger(PromotionService.class);

  private final PromotionRepository promotionRepository;
  private final PromotionFactory promotionFactory;

  public Page<Promotion> getAllPromotions(Pageable pageable) {
    return this.promotionRepository.findAll(pageable);
  }

  public Promotion getPromotionById(String id) {
    return this.promotionRepository
        .findById(id)
        .orElseThrow(
            () -> new EntityRetrieveException("Promotion not found", HttpStatus.NOT_FOUND, id));
  }

  public Promotion createPromotion(PromotionDTO promotionDTO) {
    Promotion promotion = this.promotionFactory.toEntity(promotionDTO);
    if (promotion.getProduct() == null) {
      throw new EntityRetrieveException(
          "Product not found", HttpStatus.NOT_FOUND, promotionDTO.getProductId());
    }
    return this.promotionRepository.save(promotion);
  }

  public Promotion updatePromotion(String id, PromotionDTO promotionDTO) {
    Promotion existingPromotion = this.getPromotionById(id);

    if (promotionDTO.getEnd() != null) {
      existingPromotion.setEnd(promotionDTO.getEnd());
    }

    if (promotionDTO.getBegin() != null) {
      existingPromotion.setBegin(promotionDTO.getBegin());
    }

    if (promotionDTO.getDiscount() != null) {
      existingPromotion.setDiscount(promotionDTO.getDiscount());
    }

    return null;
  }

  public void deletePromotion(String id) {
    Promotion existingPromotion = this.getPromotionById(id);
    this.promotionRepository.delete(existingPromotion);
  }

  public Sale.Position applyPromotion(Sale.Position position) {
    Promotion promotion =
        this.promotionRepository
            .findAllByProductAndBeginBeforeAndEndAfter(
                position.getProduct(), new Date(), new Date())
            .stream()
            .max(Comparator.comparingDouble(Promotion::getDiscount))
            .orElse(null);

    if (promotion == null) {
      LOGGER.debug(
          "No active promotion found for product: {} ({})",
          position.getProduct().getName(),
          position.getProduct().getId());
      return position;
    }

    LOGGER.info("Applying promotion: {} for product: {} ({})", promotion.getId(), position.getProduct().getName(), position.getProduct().getId());
    position.setDiscountEuro(promotion.getDiscount());
    return position;
  }
}
