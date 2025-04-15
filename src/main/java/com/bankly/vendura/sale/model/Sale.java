package com.bankly.vendura.sale.model;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.inventory.product.model.Product;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
  private Set<Position> positions;

  @Getter
  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Position {
    @DBRef private Product product;
    private int quantity;
    private Set<Position> connectedPositions = new HashSet<>();
    private double discountEuro;

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
                  connectedProduct ->
                      Position.builder().product(connectedProduct).quantity(this.quantity).build())
              .collect(Collectors.toSet());
    }

    public double getReducedPrice() {
      return product.getPrice() - discountEuro < 0 ? 0 : product.getPrice() - discountEuro;
    }

    public int getDiscountPercentage() {
      return (int) ((discountEuro / product.getPrice()) * 100);
    }

  }
}
