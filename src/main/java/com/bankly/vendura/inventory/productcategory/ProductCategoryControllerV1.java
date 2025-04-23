package com.bankly.vendura.inventory.productcategory;

import com.bankly.vendura.inventory.productcategory.model.ProductCategoryDTO;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryFactory;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
  @Cacheable(
          value = "productCategoryPage",
          key =
                  "'all?page=' + #pageable.getPageNumber() + ',size=' + #pageable.getPageSize() + ',sort=' + #pageable.getSort().toString()")
  public ResponseEntity<Page<ProductCategoryDTO>> getProductCategories(Pageable pageable) {
    return ResponseEntity.ok(
        this.productCategoryRepository.findAll(pageable).map(ProductCategoryFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('POS')")
  @Cacheable(value = "productCategory", key = "#id")
  public ResponseEntity<ProductCategoryDTO> getProductCategoryByID(@PathVariable("id") String id) {
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
  @Caching(evict = {@CacheEvict(value = "productCategoryPage", allEntries = true)})
  public ResponseEntity<ProductCategoryDTO> createProductCategory(
      @RequestBody ProductCategoryDTO productCategoryDTO) {
    return ResponseEntity.ok(
        ProductCategoryFactory.toDTO(this.productCategoryService.create(productCategoryDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Caching(evict = {
          @CacheEvict(value = "productCategoryPage", allEntries = true),
          @CacheEvict(value = "productCategory", key = "#id")
  })
  public ResponseEntity<ProductCategoryDTO> updateProductCategory(
      @PathVariable("id") String id, @RequestBody ProductCategoryDTO productCategoryDTO) {
    return ResponseEntity.ok(
        ProductCategoryFactory.toDTO(this.productCategoryService.update(id, productCategoryDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Caching(evict = {
          @CacheEvict(value = "productCategoryPage", allEntries = true),
          @CacheEvict(value = "productCategory", key = "#id")
  })
  public ResponseEntity<?> deleteProductCategory(@PathVariable("id") String id) {
    this.productCategoryService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
