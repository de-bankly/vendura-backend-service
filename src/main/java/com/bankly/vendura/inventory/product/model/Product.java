package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.Brand;
import com.bankly.vendura.inventory.productcategory.model.ProductCategory;
import com.bankly.vendura.inventory.supplier.model.Supplier;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id private String id;

  private String name;

  private String shortDescription;

  private String longDescription;

  @DBRef private ProductCategory productCategory;

  @DBRef private Brand brand;

  @DBRef private Supplier defaultSupplier;

  private Long minStockLevel;

  private Long maxStockLevel;

  private Long reorderPoint;

  private Long reorderQuantity;

  private Long leadTimeInDays;

  @DBRef private Set<Product> connectedProducts = new HashSet<>();

  private boolean standalone;

  private List<PriceHistory> priceHistories;

  public Set<Product> getConnectedProductsIndefinite() {
    Set<Product> connectedProductsIndefinite = new HashSet<>(this.connectedProducts);
    for (Product connectedProduct : this.connectedProducts) {
      connectedProductsIndefinite.addAll(connectedProduct.getConnectedProductsIndefinite());
    }
    return connectedProductsIndefinite;
  }

  /**
   * Gets the current price from the latest price history entry
   *
   * @return the current price or 0 if no price history exists
   */
  public double getPrice() {
    if (priceHistories == null || priceHistories.isEmpty()) {
      return 0;
    }
    return priceHistories.stream()
        .max(Comparator.comparing(PriceHistory::getTimestamp))
        .map(PriceHistory::getPrice)
        .orElse(0.0);
  }

  /**
   * Gets the current purchase price from the latest price history entry
   *
   * @return the current purchase price or 0 if no price history exists
   */
  public double getPurchasePrice() {
    if (priceHistories == null || priceHistories.isEmpty()) {
      return 0;
    }
    return priceHistories.stream()
        .max(Comparator.comparing(PriceHistory::getTimestamp))
        .map(PriceHistory::getPurchasePrice)
        .orElse(0.0);
  }

  /**
   * Gets the price at a specific point in time
   *
   * @param date The date to check for price
   * @return The price at the given date or latest price before that date
   */
  public double getPriceAtDate(Date date) {
    if (priceHistories == null || priceHistories.isEmpty()) {
      return 0;
    }
    return priceHistories.stream()
        .filter(ph -> ph.getTimestamp().before(date) || ph.getTimestamp().equals(date))
        .max(Comparator.comparing(PriceHistory::getTimestamp))
        .map(PriceHistory::getPrice)
        .orElse(0.0);
  }

  /**
   * Gets the purchase price at a specific point in time
   *
   * @param date The date to check for purchase price
   * @return The purchase price at the given date or latest price before that date
   */
  public double getPurchasePriceAtDate(Date date) {
    if (priceHistories == null || priceHistories.isEmpty()) {
      return 0;
    }
    return priceHistories.stream()
        .filter(ph -> ph.getTimestamp().before(date) || ph.getTimestamp().equals(date))
        .max(Comparator.comparing(PriceHistory::getTimestamp))
        .map(PriceHistory::getPurchasePrice)
        .orElse(0.0);
  }

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PriceHistory {
    private Date timestamp;
    private double purchasePrice;
    private double price;
    @DBRef private Supplier supplier;
  }
}
