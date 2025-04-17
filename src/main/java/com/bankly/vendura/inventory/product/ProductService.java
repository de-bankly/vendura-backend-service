package com.bankly.vendura.inventory.product;

import com.bankly.vendura.inventory.brand.model.BrandFactory;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.product.model.ProductRepository;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierFactory;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import com.bankly.vendura.utilities.exceptions.EntityCreationException;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.util.UUID;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductTransactionService productTransactionService;

  /**
   * Get all products with optional stock calculation
   * @param pageable Pagination parameters
   * @param calculateStock Whether to calculate current stock for each product
   * @return Page of product DTOs
   */
  public Page<ProductDTO> getProducts(Pageable pageable, boolean calculateStock) {
    Page<ProductDTO> productDTOPage = this.productRepository.findAll(pageable).map(ProductFactory::toDTO);

    if (calculateStock) {
      productDTOPage.getContent().forEach(productDTO -> {
        productDTO.setCurrentStock(productTransactionService.calculateCurrentStock(productDTO.getId()));
      });
    }

    return productDTOPage;
  }

  /**
   * Get a single product by ID with optional stock calculation
   * @param id Product ID
   * @param calculateStock Whether to calculate current stock for the product
   * @return Product DTO
   */
  public ProductDTO getProductById(String id, boolean calculateStock) {
    ProductDTO productDTO = ProductFactory.toDTO(
        this.productRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("Product not found", HttpStatus.NOT_FOUND, id)));

    if (calculateStock) {
      productDTO.setCurrentStock(productTransactionService.calculateCurrentStock(id));
    }

    return productDTO;
  }

  public Product create(ProductDTO productDTO) {

    if (this.productRepository.findByName(productDTO.getName()).isPresent()) {
      throw new EntityCreationException(
              "Product with that name already exists", HttpStatus.CONFLICT, productDTO.getName(), false);
    }

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
      if (this.productRepository.findById(productDTO.getId()).isPresent()) {
        throw new EntityCreationException(
                "Product with that ID already exists", HttpStatus.CONFLICT, productDTO.getName(), false);
      }
      product.setId(productDTO.getId());
    }

    if (productDTO.getName() != null) {
      if (this.productRepository.findByName(productDTO.getName()).isPresent()) {
        throw new EntityCreationException(
                "Product with that name already exists", HttpStatus.CONFLICT, productDTO.getName(), false);
      }

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

    if (productDTO.getStandalone() != null) {
      product.setStandalone(productDTO.getStandalone());
    }

    if (productDTO.getConnectedProducts() != null) {
      product.setConnectedProducts(
          productDTO.getConnectedProducts().stream()
              .map(ProductFactory::toEntity)
              .collect(Collectors.toSet()));
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

  public Product getProductEntityById(String id) {
    return this.productRepository.findById(id).orElse(null);
  }
}
