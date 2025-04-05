package com.bankly.vendura.inventory.supplierorder.model;

import com.bankly.vendura.inventory.product.model.Product;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;

import com.bankly.vendura.inventory.transactions.product.model.ProductTransactable;
import com.bankly.vendura.inventory.transactions.product.model.ProductTransaction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "supplier_orders")
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrder implements ProductTransactable {

  @Id private String id;

  private Date timestamp;

  private Set<Position> positions;

  private OrderStatus orderStatus;

  @Override
  public ProductTransaction.TransactionType getTransactionType() {
    return ProductTransaction.TransactionType.WAREHOUSE_IN;
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Position {
    @DBRef private Product product;
    private int amount;
  }

  public enum OrderStatus {
    PLACED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public SupplierOrderDTO.OrderStatus toDtoStatus() {
      return SupplierOrderDTO.OrderStatus.valueOf(name());
    }
  }

  public double getTotalPrice() {
    double currentPrice = 0;

    for (Position position : this.positions) {
      Product.PriceHistory priceHistory =
          position.getProduct().getPriceHistories().stream()
              .filter(
                  productPriceHistory -> productPriceHistory.getTimestamp().before(this.timestamp))
              .max(Comparator.comparing(Product.PriceHistory::getTimestamp))
              .orElse(null);

      if (priceHistory != null) {
        currentPrice += priceHistory.getPurchasePrice() * position.getAmount();
      }
    }

    return currentPrice;
  }
}
