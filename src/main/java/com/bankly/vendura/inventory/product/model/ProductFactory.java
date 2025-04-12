package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.BrandFactory;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierFactory;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ProductFactory {

  public static ProductDTO toDTO(Product product) {
    if (product == null) return null;
    return new ProductDTO(
        product.getId(),
        product.getName(),
        ProductCategoryFactory.toDTO(product.getProductCategory()),
        BrandFactory.toDTO(product.getBrand()),
        SupplierFactory.toDTO(product.getDefaultSupplier()),
        product.getConnectedProducts().stream()
            .map(ProductFactory::toDTO)
            .collect(Collectors.toSet()),
        product.isStandalone(),
        product.getPriceHistories().stream().map(ProductFactory::toDTO).toList(),
        null);
  }

  public static Product toEntity(ProductDTO productDTO) {
    if (productDTO == null) return null;
    return new Product(
        productDTO.getId(),
        productDTO.getName(),
        ProductCategoryFactory.toEntity(productDTO.getProductCategory()),
        BrandFactory.toEntity(productDTO.getBrand()),
        SupplierFactory.toEntity(productDTO.getDefaultSupplier()),
        productDTO.getConnectedProducts() != null
            ? new HashSet<>(
                productDTO.getConnectedProducts().stream()
                    .map(ProductFactory::toEntity)
                    .collect(Collectors.toSet()))
            : new HashSet<>(),
        productDTO.getStandalone() != null && productDTO.getStandalone(),
        productDTO.getPriceHistories() != null
            ? productDTO.getPriceHistories().stream().map(ProductFactory::toEntity).toList()
            : new ArrayList<>());
  }

  public static ProductDTO.PriceHistoryDTO toDTO(Product.PriceHistory priceHistory) {
    return new ProductDTO.PriceHistoryDTO(
        priceHistory.getTimestamp(),
        priceHistory.getPurchasePrice(),
        priceHistory.getPurchasePrice(),
        SupplierFactory.toDTO(priceHistory.getSupplier()));
  }

  public static Product.PriceHistory toEntity(ProductDTO.PriceHistoryDTO priceHistoryDTO) {
    return new Product.PriceHistory(
        priceHistoryDTO.getTimestamp(),
        priceHistoryDTO.getPurchasePrice(),
        priceHistoryDTO.getPrice(),
        SupplierFactory.toEntity(priceHistoryDTO.getSupplier()));
  }
}
