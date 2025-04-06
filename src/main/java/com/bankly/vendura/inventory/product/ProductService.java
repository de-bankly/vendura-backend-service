package com.bankly.vendura.inventory.product;

import com.bankly.vendura.inventory.brand.model.BrandFactory;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.product.model.ProductRepository;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierFactory;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

  private final ProductRepository productRepository;

  public Product create(ProductDTO productDTO) {
    Product product = ProductFactory.toEntity(productDTO);
    return this.productRepository.save(product);
  }

  public Product update(String id, ProductDTO productDTO) {
    Product product =
        this.productRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("Product not found", HttpStatus.NOT_FOUND, id));

    if (productDTO.getId() != null) {
      product.setId(productDTO.getId());
    }

    if (productDTO.getName() != null) {
      product.setName(productDTO.getName());
    }

    if (productDTO.getProductCategory() != null) {
      product.setProductCategory(ProductCategoryFactory.toEntity(productDTO.getProductCategory()));
    }

    if (productDTO.getBrand() != null) {
      product.setBrand(BrandFactory.toEntity(productDTO.getBrand()));
    }

    if (productDTO.getDefaultSupplier() != null) {
      product.setDefaultSupplier(SupplierFactory.toEntity(productDTO.getDefaultSupplier()));
    }

    if (productDTO.getPriceHistories() != null) {
      product.setPriceHistories(
          productDTO.getPriceHistories().stream().map(ProductFactory::toEntity).toList());
    }

    return this.productRepository.save(product);
  }


  public void delete(String id) {
    Product product =
            this.productRepository
                    .findById(id)
                    .orElseThrow(
                            () -> new EntityRetrieveException("Product not found", HttpStatus.NOT_FOUND, id));

    this.productRepository.delete(product);
  }
}
