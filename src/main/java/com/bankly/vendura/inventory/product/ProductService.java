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
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductTransactionService productTransactionService;

  /**
   * Get all products with optional stock calculation
   *
   * @param pageable Pagination parameters
   * @param calculateStock Whether to calculate current stock for each product
   * @return Page of product DTOs
   */
  public Page<ProductDTO> getProducts(Pageable pageable, boolean calculateStock) {
    Page<ProductDTO> productDTOPage =
        this.productRepository.findAll(pageable).map(ProductFactory::toDTO);

    if (calculateStock) {
      productDTOPage
          .getContent()
          .forEach(
              productDTO -> {
                productDTO.setCurrentStock(
                    productTransactionService.calculateCurrentStock(productDTO.getId()));
              });
    }

    return productDTOPage;
  }

  /**
   * Get a single product by ID with optional stock calculation
   *
   * @param id Product ID
   * @param calculateStock Whether to calculate current stock for the product
   * @return Product DTO
   */
  public ProductDTO getProductById(String id, boolean calculateStock) {
    ProductDTO productDTO =
        ProductFactory.toDTO(
            this.productRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "Product not found", HttpStatus.NOT_FOUND, id)));

    if (calculateStock) {
      productDTO.setCurrentStock(productTransactionService.calculateCurrentStock(id));
    }

    return productDTO;
  }

  @Transactional
  public Product create(ProductDTO productDTO) {
    if (this.productRepository.findByName(productDTO.getName()).isPresent()) {
      throw new EntityCreationException(
          "Product with that name already exists",
          HttpStatus.CONFLICT,
          productDTO.getName(),
          false);
    }

    Product product = ProductFactory.toEntity(productDTO);

    if (product.getId() == null) {
      product.setId(generateUniqueProductId());
    }

    Set<Product> loadedConnectedProducts = new HashSet<>();
    if (productDTO.getConnectedProducts() != null && !productDTO.getConnectedProducts().isEmpty()) {
      Set<String> connectedProductIds =
          productDTO.getConnectedProducts().stream()
              .map(ProductDTO::getId)
              .filter(id -> id != null && !id.trim().isEmpty())
              .collect(Collectors.toSet());

      for (String connectedId : connectedProductIds) {
        if (connectedId.equals(product.getId())) {
          throw new EntityCreationException(
              "A product cannot be connected to itself.",
              HttpStatus.BAD_REQUEST,
              connectedId,
              false);
        }

        Product connectedProduct =
            this.productRepository
                .findById(connectedId)
                .orElseThrow(
                    () ->
                        new EntityCreationException(
                            "Connected product not found with ID: " + connectedId,
                            HttpStatus.NOT_FOUND,
                            connectedId,
                            false));

        if (createsCycle(product.getId(), connectedProduct)) {
          throw new EntityCreationException(
              "Cyclic connection detected: product "
                  + connectedId
                  + " would create a cycle with "
                  + product.getId(),
              HttpStatus.BAD_REQUEST,
              connectedId,
              false);
        }

        loadedConnectedProducts.add(connectedProduct);
      }

      if (loadedConnectedProducts.size() != connectedProductIds.size()) {
        throw new EntityCreationException(
            "Could not find all connected products or duplicate IDs provided.",
            HttpStatus.NOT_FOUND,
            String.join(",", connectedProductIds),
            false);
      }
    }

    product.setConnectedProducts(loadedConnectedProducts);

    return this.productRepository.save(product);
  }

  /**
   * Generates a unique product ID that doesn't already exist in the database
   *
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

  private boolean createsCycle(String productId, Product connectedProduct) {
    Set<String> visited = new HashSet<>();
    return hasCycleRecursive(productId, connectedProduct, visited);
  }

  private boolean hasCycleRecursive(String targetId, Product current, Set<String> visited) {
    if (current.getId().equals(targetId)) {
      return true;
    }

    if (!visited.add(current.getId())) {
      return false; // bereits besucht
    }

    for (Product next : current.getConnectedProducts()) {
      if (hasCycleRecursive(targetId, next, visited)) {
        return true;
      }
    }

    return false;
  }

  private Set<Product> loadAndValidateConnectedProducts(
      String currentProductId, Set<ProductDTO> connectedDTOs) {
    Set<Product> connectedProducts = new HashSet<>();
    Set<String> connectedIds =
        connectedDTOs.stream()
            .map(ProductDTO::getId)
            .filter(id -> id != null && !id.trim().isEmpty())
            .collect(Collectors.toSet());

    for (String connectedId : connectedIds) {
      if (connectedId.equals(currentProductId)) {
        throw new EntityCreationException(
            "A product cannot be connected to itself.", HttpStatus.BAD_REQUEST, connectedId, false);
      }

      Product connectedProduct =
          this.productRepository
              .findById(connectedId)
              .orElseThrow(
                  () ->
                      new EntityCreationException(
                          "Connected product not found: " + connectedId,
                          HttpStatus.NOT_FOUND,
                          connectedId,
                          false));

      if (connectedProduct.getConnectedProducts().stream()
          .anyMatch(p -> p.getId().equals(currentProductId))) {
        throw new EntityCreationException(
            "Circular connection detected between product "
                + currentProductId
                + " and "
                + connectedId,
            HttpStatus.BAD_REQUEST,
            connectedId,
            false);
      }

      connectedProducts.add(connectedProduct);
    }

    return connectedProducts;
  }

  @Transactional
  public Product update(String id, ProductDTO productDTO) {
    Product product =
        this.productRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("Product not found", HttpStatus.NOT_FOUND, id));

    if (productDTO.getId() != null && !productDTO.getId().equals(id)) {
      if (this.productRepository.existsById(productDTO.getId())) {
        throw new EntityCreationException(
            "Product with the target ID '" + productDTO.getId() + "' already exists",
            HttpStatus.CONFLICT,
            productDTO.getId(),
            false);
      }
    }

    if (productDTO.getName() != null && !productDTO.getName().equals(product.getName())) {
      this.productRepository
          .findByName(productDTO.getName())
          .filter(existing -> !existing.getId().equals(id))
          .ifPresent(
              existing -> {
                throw new EntityCreationException(
                    "Product with name '" + productDTO.getName() + "' already exists",
                    HttpStatus.CONFLICT,
                    productDTO.getName(),
                    false);
              });
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
      Set<Product> connectedProducts =
          loadAndValidateConnectedProducts(id, productDTO.getConnectedProducts());
      product.setConnectedProducts(connectedProducts);
    }

    return this.productRepository.save(product);
  }

  @Transactional
  public void delete(String id) {
    Product product =
        this.productRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("Product not found", HttpStatus.NOT_FOUND, id));

    this.productRepository.delete(product);
  }
}
