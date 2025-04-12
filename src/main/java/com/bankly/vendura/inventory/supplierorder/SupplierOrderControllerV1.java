package com.bankly.vendura.inventory.supplierorder;

import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderDTO;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderFactory;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/supplierorder")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupplierOrderControllerV1 {

  private final SupplierOrderService supplierOrderService;
  private final SupplierOrderRepository supplierOrderRepository;

  @GetMapping
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<Page<SupplierOrderDTO>> getSupplierOrders(Pageable pageable) {
    return ResponseEntity.ok(
        this.supplierOrderRepository.findAll(pageable).map(SupplierOrderFactory::toDTO));
  }

  @GetMapping("/{id}")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<SupplierOrderDTO> getSupplierOrderById(@PathVariable("id") String id) {
    return ResponseEntity.ok(
        SupplierOrderFactory.toDTO(
            this.supplierOrderRepository
                .findById(id)
                .orElseThrow(
                    () ->
                        new EntityRetrieveException(
                            "SupplierOrder not found", HttpStatus.NOT_FOUND, id))));
  }

  @PostMapping
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<SupplierOrderDTO> createSupplierOrder(
      @RequestBody SupplierOrderDTO supplierOrderDTO) {
    return ResponseEntity.ok(
        SupplierOrderFactory.toDTO(this.supplierOrderService.create(supplierOrderDTO)));
  }

  @PutMapping("/{id}")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<SupplierOrderDTO> updateSupplierOrder(
      @PathVariable("id") String id,
      @RequestBody SupplierOrderDTO supplierOrderDTO,
      Authentication authentication) {
    return ResponseEntity.ok(
        SupplierOrderFactory.toDTO(
            this.supplierOrderService.update(id, supplierOrderDTO, authentication.getName())));
  }

  @DeleteMapping("/{id}")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<?> deleteSupplierOrder(@PathVariable("id") String id) {
    this.supplierOrderService.delete(id);
    return ResponseEntity.noContent().build();
  }
}
