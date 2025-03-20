package com.bankly.vendura.inventory.product.model;

public class ProductCategoryFactory {

  public static ProductCategoryDTO toDTO(ProductCategory productCategory) {
    return new ProductCategoryDTO(productCategory.getId(), productCategory.getName());
  }

  public static ProductCategory toEntity(ProductCategoryDTO productCategoryDTO) {
    return new ProductCategory(productCategoryDTO.getId(), productCategoryDTO.getName());
  }
}
