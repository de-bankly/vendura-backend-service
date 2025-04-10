package com.bankly.vendura.inventory.productcategory;

import com.bankly.vendura.inventory.productcategory.model.ProductCategory;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryDTO;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryRepository;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductCategoryService {

  private final ProductCategoryRepository productCategoryRepository;

  public ProductCategory create(ProductCategoryDTO productCategoryDTO) {

    if (this.productCategoryRepository.findByName(productCategoryDTO.getName()).isPresent()) {
      throw new EntityCreationException(
          "ProductCategory with that name already exists",
          HttpStatus.CONFLICT,
          productCategoryDTO.getName(),
          false);
    }

    ProductCategory productCategory = new ProductCategory();
    productCategory.setName(productCategoryDTO.getName());
    return this.productCategoryRepository.save(productCategory);
  }

  public ProductCategory update(String id, ProductCategoryDTO productCategoryDTO) {
    ProductCategory productCategory =
        this.productCategoryRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Product category not found", HttpStatus.NOT_FOUND, id));

    if (productCategoryDTO.getName() != null) {

      if (this.productCategoryRepository.findByName(productCategoryDTO.getName()).isPresent()) {
        throw new EntityCreationException(
            "ProductCategory with that name already exists",
            HttpStatus.CONFLICT,
            productCategoryDTO.getName(),
            false);
      }

      productCategory.setName(productCategoryDTO.getName());
    }

    return this.productCategoryRepository.save(productCategory);
  }

  public void delete(String id) {
    ProductCategory productCategory =
        this.productCategoryRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Product category not found", HttpStatus.NOT_FOUND, id));

    this.productCategoryRepository.delete(productCategory);
  }
}
