package com.bankly.vendura.sale.model;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.deposit.model.DepositReceipt;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.payment.model.Payment;
import java.util.*;
import java.util.stream.Collectors;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "sales")
public class Sale {

  @Id private String id;

  private Date date;
  @DBRef private User cashier;
  private Set<Position> positions = new HashSet<>();

  @DBRef private Set<Payment> payments = new HashSet<>();
  @DBRef private Set<DepositReceipt> depositReceipts = new HashSet<>();

  public double calculateTotal() {
    return this.positions.stream().mapToDouble(Position::getPositionTotal).sum()
        - this.depositReceipts.stream().mapToDouble(DepositReceipt::calculateTotal).sum();
  }

  public long getAmountOfItems() {
    return this.positions.stream().mapToLong(Position::getTotalQuantity).sum();
  }

  public double getTotalDiscount() {
    return this.positions.stream().mapToDouble(Position::getDiscountEuro).sum();
  }

  public static SaleBuilder builder() {
    return new SaleBuilder();
  }

  public static class SaleBuilder {
    private Sale sale = new Sale();
    private SaleBuilder() {
    }

    public Sale build() {
      return sale;
    }

    public SaleBuilder id(String id) {
      this.sale.setId(id);
      return this;
    }

    public SaleBuilder date(Date date) {
      this.sale.setDate(date);
      return this;
    }

    public SaleBuilder cashier(User cashier) {
      this.sale.setCashier(cashier);
      return this;
    }

  }

  @Getter
  @NoArgsConstructor
  public static class Position {
    @DBRef private Product product;
    private int quantity;
    private Set<Position> connectedPositions = new HashSet<>();

    @Setter
    private double discountEuro;

    public Position(Product product, int quantity, double discountEuro) {
      this.setProduct(product);
      this.setQuantity(quantity);
      this.discountEuro = discountEuro;
    }

    public void setQuantity(int quantity) {
      this.quantity = quantity;
      for (Position connectedPosition : this.connectedPositions) {
        connectedPosition.setQuantity(quantity);
      }
    }

    public void setProduct(Product product) {
      this.product = product;
      this.connectedPositions =
          product.getConnectedProducts().stream()
              .map(
                  connectedProduct -> {
                    Position connectedPosition = new Position();
                    connectedPosition.setProduct(connectedProduct);
                    connectedPosition.setQuantity(quantity);
                    return connectedPosition;
                  })
              .collect(Collectors.toSet());
    }

    public double getReducedPrice() {
      return product.getPrice() - discountEuro < 0 ? 0 : product.getPrice() - discountEuro;
    }

    public double getPositionTotal() {
      return getReducedPrice() * quantity;
    }

    public int getDiscountPercentage() {
      return (int) ((discountEuro / product.getPrice()) * 100);
    }

    public long getTotalQuantity() {
      long myQuantity = this.quantity;
        for (Position connectedPosition : this.connectedPositions) {
            myQuantity += connectedPosition.getTotalQuantity();
        }
        return myQuantity;
    }
  }
}
