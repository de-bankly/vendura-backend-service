package com.bankly.vendura.inventory.brand;

import com.bankly.vendura.inventory.brand.model.BrandDTO;
import com.bankly.vendura.inventory.brand.model.BrandFactory;
import com.bankly.vendura.inventory.brand.model.BrandRepository;
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
@RequestMapping("/v1/brand")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BrandControllerV1 {

  private final BrandService brandService;
  private final BrandRepository brandRepository;

  @GetMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Cacheable(
      value = "brandPage",
      key =
          "'all?page=' + #pageable.getPageNumber() + ',size=' + #pageable.getPageSize() + ',sort=' + #pageable.getSort().toString()")
  public ResponseEntity<Page<BrandDTO>> getBrands(Pageable pageable) {
    return ResponseEntity.ok(this.brandRepository.findAll(pageable).map(BrandFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('POS')")
  @Cacheable(value = "brand", key = "#id")
  public ResponseEntity<BrandDTO> getBrandById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        BrandFactory.toDTO(
            this.brandRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException("Brand not found", HttpStatus.NOT_FOUND, id))));
  }

  @PostMapping
  @PreAuthorize("hasRole('ADMIN')")
  @Caching(evict = {@CacheEvict(value = "brandPage", allEntries = true)})
  public ResponseEntity<BrandDTO> createBrand(@RequestBody BrandDTO brandDTO) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(BrandFactory.toDTO(this.brandService.createBrand(brandDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Caching(
      evict = {
        @CacheEvict(value = "brandPage", allEntries = true),
        @CacheEvict(value = "brand", key = "#id")
      })
  public ResponseEntity<BrandDTO> updateBrand(
      @PathVariable("id") String id, @RequestBody BrandDTO brandDTO) {
    return ResponseEntity.ok(BrandFactory.toDTO(this.brandService.updateBrand(id, brandDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('ADMIN')")
  @Caching(
      evict = {
        @CacheEvict(value = "brandPage", allEntries = true),
        @CacheEvict(value = "brand", key = "#id")
      })
  public ResponseEntity<?> deleteBrand(@PathVariable("id") String id) {
    this.brandService.deleteBrand(id);
    return ResponseEntity.noContent().build();
  }
}
