package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.Brand;
import com.bankly.vendura.inventory.supply.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id private String id;

  private String name;

  @DBRef private ProductCategory productCategory;

  @DBRef private Brand brand;

  @DBRef private Supplier defaultSupplier;

  private List<PriceHistory> priceHistories;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class PriceHistory {
    private long timestamp;
    private double purchasePrice;
    private double price;
    @DBRef private Supplier supplier;
  }

}
