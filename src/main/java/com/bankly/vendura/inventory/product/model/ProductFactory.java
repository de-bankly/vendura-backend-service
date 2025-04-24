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

    ProductDTO productDTO = new ProductDTO();
    productDTO.setId(product.getId());
    productDTO.setName(product.getName());
    productDTO.setShortDescription(product.getShortDescription());
    productDTO.setLongDescription(product.getLongDescription());
    productDTO.setProductCategory(ProductCategoryFactory.toDTO(product.getProductCategory()));
    productDTO.setBrand(BrandFactory.toDTO(product.getBrand()));
    productDTO.setDefaultSupplier(SupplierFactory.toDTO(product.getDefaultSupplier()));
    productDTO.setMinStockLevel(product.getMinStockLevel());
    productDTO.setMaxStockLevel(product.getMaxStockLevel());
    productDTO.setReorderPoint(product.getReorderPoint());
    productDTO.setReorderQuantity(product.getReorderQuantity());
    productDTO.setLeadTimeInDays(product.getLeadTimeInDays());
    productDTO.setConnectedProducts(
        product.getConnectedProducts().stream()
            .map(ProductFactory::toDTO)
            .collect(Collectors.toSet()));
    productDTO.setStandalone(product.isStandalone());
    
    // Transform priceHistories if available
    if (product.getPriceHistories() != null) {
      productDTO.setPriceHistories(
          product.getPriceHistories().stream().map(ProductFactory::toDTO).collect(Collectors.toList()));
    }

    return productDTO;
  }

  public static Product toEntity(ProductDTO productDTO) {
    if (productDTO == null) return null;

    Product product = new Product();
    product.setId(productDTO.getId());
    product.setName(productDTO.getName());
    product.setShortDescription(productDTO.getShortDescription());
    product.setLongDescription(productDTO.getLongDescription());
    product.setProductCategory(ProductCategoryFactory.toEntity(productDTO.getProductCategory()));
    product.setBrand(BrandFactory.toEntity(productDTO.getBrand()));
    product.setDefaultSupplier(SupplierFactory.toEntity(productDTO.getDefaultSupplier()));
    product.setMinStockLevel(productDTO.getMinStockLevel());
    product.setMaxStockLevel(productDTO.getMaxStockLevel());
    product.setReorderPoint(productDTO.getReorderPoint());
    product.setReorderQuantity(productDTO.getReorderQuantity());
    product.setLeadTimeInDays(productDTO.getLeadTimeInDays());
    
    if (productDTO.getConnectedProducts() != null) {
      product.setConnectedProducts(
          productDTO.getConnectedProducts().stream()
              .map(ProductFactory::toEntity)
              .collect(Collectors.toSet()));
    }
    
    product.setStandalone(productDTO.getStandalone() != null ? productDTO.getStandalone() : true);
    
    // Transform priceHistories if available
    if (productDTO.getPriceHistories() != null) {
      product.setPriceHistories(
          productDTO.getPriceHistories().stream().map(ProductFactory::toEntity).collect(Collectors.toList()));
    }

    return product;
  }

  public static ProductDTO.PriceHistoryDTO toDTO(Product.PriceHistory priceHistory) {
    return new ProductDTO.PriceHistoryDTO(
        priceHistory.getTimestamp(),
        priceHistory.getPurchasePrice(),
        priceHistory.getPrice(),
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
