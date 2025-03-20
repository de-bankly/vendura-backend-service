package com.bankly.vendura.inventory.product;

import com.bankly.vendura.inventory.product.model.ProductCategoryDTO;
import com.bankly.vendura.inventory.product.model.ProductCategoryFactory;
import com.bankly.vendura.inventory.product.model.ProductCategoryRepository;
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
@RequestMapping("/v1/productcategory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductCategoryControllerV1 {

  private final ProductCategoryService productCategoryService;
  private final ProductCategoryRepository productCategoryRepository;

  @GetMapping
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<Page<ProductCategoryDTO>> getProductCategories(Pageable pageable) {
    return ResponseEntity.ok(
        this.productCategoryRepository.findAll(pageable).map(ProductCategoryFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('POS')")
  public ResponseEntity<ProductCategoryDTO> getProductCategoryByID(@RequestParam("id") String id) {
    return ResponseEntity.ok(
        ProductCategoryFactory.toDTO(
            this.productCategoryRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "Product category not found", HttpStatus.NOT_FOUND, id))));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductCategoryDTO> createProductCategory(
      @RequestBody ProductCategoryDTO productCategoryDTO) {
    return ResponseEntity.ok(
        ProductCategoryFactory.toDTO(this.productCategoryService.create(productCategoryDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<ProductCategoryDTO> updateProductCategory(
      @RequestParam("id") String id, @RequestBody ProductCategoryDTO productCategoryDTO) {
    return ResponseEntity.ok(
        ProductCategoryFactory.toDTO(this.productCategoryService.update(id, productCategoryDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  public ResponseEntity<?> deleteProductCategory(@RequestParam("id") String id) {
    this.productCategoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
