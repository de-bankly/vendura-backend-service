package com.bankly.vendura.inventory.product;

import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.product.model.ProductRepository;
import com.bankly.vendura.inventory.supplier.model.SupplierRepository;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/product")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductControllerV1 {

  private final ProductService productService;
  private final ProductRepository productRepository;

  private final ProductTransactionService productTransactionService;

  private final SupplierRepository supplierRepository;

  @GetMapping
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<Page<ProductDTO>> getProducts(Pageable pageable) {
    return ResponseEntity.ok(this.productRepository.findAll(pageable).map(ProductFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<ProductDTO> getProductByID(
      @PathVariable("id") String id,
      @RequestParam(required = false, name = "calculateStock", defaultValue = "false")
          boolean calculateStock) {

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

    return ResponseEntity.ok(productDTO);
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
    return ResponseEntity.ok(ProductFactory.toDTO(this.productService.create(productDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductDTO> updateProduct(
      @PathVariable("id") String id, @RequestBody ProductDTO productDTO) {
    return ResponseEntity.ok(ProductFactory.toDTO(this.productService.update(id, productDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteProduct(@PathVariable("id") String id) {
    this.productService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
