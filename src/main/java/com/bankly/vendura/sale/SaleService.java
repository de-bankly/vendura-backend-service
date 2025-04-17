package com.bankly.vendura.sale;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.inventory.promotion.PromotionService;
import com.bankly.vendura.payment.PaymentService;
import com.bankly.vendura.sale.model.Sale;
import com.bankly.vendura.sale.model.SaleDTO;
import com.bankly.vendura.sale.model.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SaleService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleService.class);

  private final DynamicSaleFactory dynamicSaleFactory;
  private final PaymentService paymentService;
  private final SaleRepository saleRepository;
  private final PromotionService promotionService;

  public void applyDiscountsToSale(Sale sale) {
    LOGGER.info("Applying discounts to sale with ID: {}", sale.getId());
    for (Sale.Position position : sale.getPositions()) {
      this.promotionService.applyPromotion(position);
    }
  }

  public Object submitSaleToProcess(SaleDTO saleDTO) {
    Sale sale =
        this.dynamicSaleFactory.createSale(
            saleDTO, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

    this.applyDiscountsToSale(sale);

    try {
      boolean paymentSuccess = this.paymentService.handlePaymentsOnSale(sale);

      if (!paymentSuccess) {
        throw new IllegalArgumentException("Payment processing failed");
      }

    } catch (IllegalArgumentException e) {
      throw e;
    }

    this.saleRepository.save(sale);

    // TODO remove products from the warehouse

    return this.dynamicSaleFactory.toDTO(sale);
  }
}
