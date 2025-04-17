package com.bankly.vendura.deposit.model;

import com.bankly.vendura.inventory.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "depositReceipts")
public class DepositReceipt {

    @Id private String id;

  private Set<Position> positions = new HashSet<>();

  public double calculateTotal() {
    return this.positions.stream().mapToDouble(Position::getPositionTotal).sum();
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
    public static class Position {
        int quantity;
        @DBRef
        Product product;

        public double getPositionTotal() {
            return this.product.getPrice() * this.quantity;
        }

    }

}
