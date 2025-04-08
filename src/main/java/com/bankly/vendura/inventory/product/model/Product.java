package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.Brand;
import com.bankly.vendura.inventory.productcategory.model.ProductCategory;
import com.bankly.vendura.inventory.supplier.model.Supplier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Document(collection = "products")
@NoArgsConstructor
@AllArgsConstructor
public class Product {

  @Id private String id;

  private String name;

  private String shortDescription;

  private String longDescription;

  private String sku;

  @DBRef private ProductCategory productCategory;

  @DBRef private Brand brand;

  @DBRef private Supplier defaultSupplier;

  private Long minStockLevel;

  private Long maxStockLevel;

  private Long reorderPoint;

  private Long reorderQuantity;

  private Long leadTimeInDays;

  private double price;

  private double purchasePrice;

  @DBRef private Set<Product> connectedProducts = new HashSet<>();

  private boolean standalone;

  private List<PriceHistory> priceHistories;

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
