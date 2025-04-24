package com.bankly.vendura.sale;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.deposit.model.DepositReceiptRepository;
import com.bankly.vendura.inventory.promotion.PromotionService;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import com.bankly.vendura.payment.PaymentService;
import com.bankly.vendura.sale.model.Sale;
import com.bankly.vendura.sale.model.SaleDTO;
import com.bankly.vendura.sale.model.SaleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SaleService {

  private static final Logger LOGGER = LoggerFactory.getLogger(SaleService.class);

  private final DynamicSaleFactory dynamicSaleFactory;
  private final PaymentService paymentService;
  private final SaleRepository saleRepository;
  private final PromotionService promotionService;
  private final ProductTransactionService productTransactionService;
  private final DepositReceiptRepository depositReceiptRepository;

  public void applyDiscountsToSale(Sale sale) {
    LOGGER.info("Applying discounts to sale with ID: {}", sale.getId());
    for (Sale.Position position : sale.getPositions()) {
      this.promotionService.applyPromotion(position);
    }
  }

  @Transactional
  public Object submitSaleToProcess(SaleDTO saleDTO) {
    Sale sale =
        this.dynamicSaleFactory.createSale(
            saleDTO, (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
    this.saleRepository.save(sale);

    this.applyDiscountsToSale(sale);
    this.productTransactionService.handleSale(sale);

    try {
      boolean paymentSuccess = this.paymentService.handlePaymentsOnSale(sale);

      if (!paymentSuccess) {
        this.saleRepository.delete(sale);
        throw new IllegalArgumentException("Payment processing failed");
      }

    } catch (IllegalArgumentException e) {
      this.saleRepository.delete(sale);
      throw e;
    }

    if (!sale.getDepositReceipts().isEmpty()) {
      sale.getDepositReceipts().forEach(receipt -> receipt.setRedeemed(true));
      this.depositReceiptRepository.saveAll(sale.getDepositReceipts());
    }

    this.saleRepository.save(sale);

    return this.dynamicSaleFactory.toDTO(sale);
  }

  @PostConstruct
  public void init() {
    // Initialize any necessary components or configurations here
    LOGGER.info("SaleService initialized");
    for (Sale sale : this.saleRepository.findAll()) {
      if (sale.getPositions().stream().anyMatch(position -> position.getProduct() == null)) {
        this.saleRepository.delete(sale);
        LOGGER.warn("Deleted sale with ID {} due to null product in position", sale.getId());
      }
    }
  }
}
