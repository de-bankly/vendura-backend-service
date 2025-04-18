package com.bankly.vendura.sale;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.deposit.model.DepositReceipt;
import com.bankly.vendura.deposit.model.DepositReceiptDTO;
import com.bankly.vendura.deposit.model.DepositReceiptFactory;
import com.bankly.vendura.deposit.model.DepositReceiptRepository;
import com.bankly.vendura.inventory.product.ProductService;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.payment.PaymentService;
import com.bankly.vendura.payment.giftcard.model.GiftCardRepository;
import com.bankly.vendura.payment.model.Payment;
import com.bankly.vendura.payment.model.PaymentDTO;
import com.bankly.vendura.payment.model.PaymentFactory;
import com.bankly.vendura.sale.model.Sale;
import com.bankly.vendura.sale.model.SaleDTO;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class DynamicSaleFactory {

  private final ProductService productService;
  private final UserRepository userRepository;
  private final GiftCardRepository giftCardRepository;
  private final PaymentService paymentService;
  private final DepositReceiptRepository depositReceiptRepository;

  private void validateAndPrepareIncomingSaleDTO(SaleDTO saleDTO, User user) {

    // IDs will be auto-generated and cannot be set
    if (saleDTO.getId() != null) {
      throw new IllegalArgumentException("Sale ID should not be provided");
    }

    // Setting date to now
    saleDTO.setDate(new Date());

    // Setting cashierId to currently authenticated user
    saleDTO.setCashierId(user.getId());

    // Positions cannot be empty
    if (saleDTO.getPositions() == null || saleDTO.getPositions().isEmpty()) {
      throw new IllegalArgumentException("Sale must have at least one position");
    }

    // Payments cannot be empty
    if (saleDTO.getPayments() == null || saleDTO.getPayments().isEmpty()) {
      throw new IllegalArgumentException("Sale must have at least one payment");
    }

    for (SaleDTO.PositionDTO position : saleDTO.getPositions()) {
      if (position.getProductDTO() == null) {
        throw new IllegalArgumentException("Product cannot be null");
      }
      if (position.getProductDTO().getId() == null) {
        throw new IllegalArgumentException("Product ID cannot be null");
      }
    }
  }

  public Sale createSale(SaleDTO saleDTO, User user) {
    validateAndPrepareIncomingSaleDTO(saleDTO, user);

    Sale.SaleBuilder saleBuilder = Sale.builder();
    saleBuilder.date(saleDTO.getDate()).cashier(user);

    Sale sale = saleBuilder.build();

    for (SaleDTO.PositionDTO positionDTO : saleDTO.getPositions()) {
      Product product =
          this.productService.getProductEntityById(positionDTO.getProductDTO().getId());
      if (product == null) {
        throw new IllegalArgumentException("Product not found");
      }

      Sale.Position createdPosition =
          new Sale.Position(product, positionDTO.getQuantity(), positionDTO.getDiscountEuro());
      sale.getPositions().add(createdPosition);
    }

    for (PaymentDTO paymentDTO : saleDTO.getPayments()) {
      paymentDTO.setIssuerId(user.getId());
      Payment payment =
          PaymentFactory.toEntity(paymentDTO, this.userRepository, this.giftCardRepository);
      this.paymentService.createPayment(payment);
      sale.getPayments().add(payment);
    }

    if (!saleDTO.getDepositReceipts().isEmpty()) {
      List<DepositReceipt> depositReceipts =
              this.depositReceiptRepository.findAllById(saleDTO.getDepositReceipts().stream().map(DepositReceiptDTO::getId).collect(Collectors.toSet()));
      sale.getDepositReceipts().addAll(depositReceipts);
    }

    return sale;
  }

  public SaleDTO toDTO(Sale sale) {
    SaleDTO dto =
        SaleDTO.builder()
            .id(sale.getId())
            .date(sale.getDate())
            .cashierId(sale.getCashier().getId())
            .build();

    dto.setTotal(sale.calculateTotal());

    dto.getPayments()
        .addAll(sale.getPayments().stream().map(PaymentFactory::toDTO).collect(Collectors.toSet()));

    dto.getPositions()
        .addAll(sale.getPositions().stream().map(this::toDTO).collect(Collectors.toSet()));

    dto.getDepositReceipts().addAll(
        sale.getDepositReceipts().stream()
            .map(DepositReceiptFactory::toDTO)
            .collect(Collectors.toSet()));

    return dto;
  }

  public SaleDTO.PositionDTO toDTO(Sale.Position entityPosition) {
    SaleDTO.PositionDTO dtoPosition =
        SaleDTO.PositionDTO.builder()
            .productDTO(ProductFactory.toDTO(entityPosition.getProduct()))
            .quantity(entityPosition.getQuantity())
            .build();

    dtoPosition
        .getConnectedPositions()
        .addAll(
            entityPosition.getConnectedPositions().stream()
                .map(this::toDTO)
                .collect(Collectors.toSet()));

    dtoPosition.setDiscountEuro(entityPosition.getDiscountEuro());

    return dtoPosition;
  }
}
