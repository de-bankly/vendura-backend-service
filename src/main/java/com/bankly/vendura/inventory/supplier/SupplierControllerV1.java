package com.bankly.vendura.inventory.supplier;

import com.bankly.vendura.inventory.supplier.model.SupplierDTO;
import com.bankly.vendura.inventory.supplier.model.SupplierFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierRepository;
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
@RequestMapping("/v1/supplier")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupplierControllerV1 {

  private final SupplierService supplierService;
  private final SupplierRepository supplierRepository;

  @GetMapping
  @PreAuthorize("hasRole('INVENTORY')")
  @Cacheable(
      value = "supplierPage",
      key =
          "'all?page=' + #pageable.getPageNumber() + ',size=' + #pageable.getPageSize() + ',sort=' + #pageable.getSort().toString()")
  public ResponseEntity<Page<SupplierDTO>> getSuppliers(Pageable pageable) {
    return ResponseEntity.ok(this.supplierRepository.findAll(pageable).map(SupplierFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('INVENTORY')")
  @Cacheable(value = "supplier", key = "#id")
  public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        this.supplierRepository
            .findById(id)
            .map(SupplierFactory::toDTO)
            .orElseThrow(
                () -> new EntityRetrieveException("Supplier not found", HttpStatus.NOT_FOUND, id)));
  }

  @PostMapping
  @PreAuthorize("hasRole('INVENTORY')")
  @Caching(evict = {@CacheEvict(value = "supplierPage", allEntries = true)})
  public ResponseEntity<SupplierDTO> createSupplier(@RequestBody SupplierDTO supplierDTO) {
    return ResponseEntity.ok(
        SupplierFactory.toDTO(this.supplierService.createSupplier(supplierDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('INVENTORY')")
  @Caching(
      evict = {
        @CacheEvict(value = "supplierPage", allEntries = true),
        @CacheEvict(value = "supplier", key = "#id")
      })
  public ResponseEntity<SupplierDTO> updateSupplier(
      @PathVariable("id") String id, @RequestBody SupplierDTO supplierDTO) {
    return ResponseEntity.ok(
        SupplierFactory.toDTO(this.supplierService.updateSupplier(id, supplierDTO)));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('INVENTORY')")
  @Caching(
      evict = {
        @CacheEvict(value = "supplierPage", allEntries = true),
        @CacheEvict(value = "supplier", key = "#id")
      })
  public ResponseEntity<?> deleteSupplier(@PathVariable("id") String id) {
    this.supplierRepository.deleteById(id);
    return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
  }
}
