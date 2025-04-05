package com.bankly.vendura.inventory.productcategory.model;

public class ProductCategoryFactory {

  public static ProductCategoryDTO toDTO(ProductCategory productCategory) {
    if (productCategory == null) return null;
    return new ProductCategoryDTO(productCategory.getId(), productCategory.getName());
  }

  public static ProductCategory toEntity(ProductCategoryDTO productCategoryDTO) {
    if (productCategoryDTO == null) return null;
    return new ProductCategory(productCategoryDTO.getId(), productCategoryDTO.getName());
  }
}
