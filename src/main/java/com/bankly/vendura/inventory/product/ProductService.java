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
import java.util.UUID;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

  private final ProductRepository productRepository;

  public Product create(ProductDTO productDTO) {
    Product product = ProductFactory.toEntity(productDTO);
    
    // Auto-generate ID if it's null
    if (product.getId() == null) {
      product.setId(generateUniqueProductId());
    }
    
    return this.productRepository.save(product);
  }
  
  /**
   * Generates a unique product ID that doesn't already exist in the database
   * @return A unique formatted product ID
   */
  private String generateUniqueProductId() {
    String productId;
    do {
      // Generate an 8-character ID
      productId = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
      // Check if this ID already exists
    } while (this.productRepository.existsById(productId));
    
    return productId;
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
