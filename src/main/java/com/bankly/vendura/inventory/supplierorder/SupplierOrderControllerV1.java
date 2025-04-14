package com.bankly.vendura.inventory.supplierorder;

import com.bankly.vendura.inventory.supplierorder.model.SupplierOrder;
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

import java.util.List;
import java.util.stream.Collectors;

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

  @GetMapping("/status/{status}")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<Page<SupplierOrderDTO>> getSupplierOrdersByStatus(
      @PathVariable("status") String status, Pageable pageable) {
    try {
      SupplierOrder.OrderStatus orderStatus = SupplierOrder.OrderStatus.valueOf(status.toUpperCase());
      return ResponseEntity.ok(
          this.supplierOrderService.getSupplierOrdersByStatus(orderStatus, pageable).map(SupplierOrderFactory::toDTO));
    } catch (IllegalArgumentException e) {
      throw new EntityRetrieveException("Invalid order status", HttpStatus.BAD_REQUEST, status);
    }
  }
  
  @GetMapping("/supplier/{supplierId}")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<Page<SupplierOrderDTO>> getSupplierOrdersBySupplierId(
      @PathVariable("supplierId") String supplierId, Pageable pageable) {
    return ResponseEntity.ok(
        this.supplierOrderService.getSupplierOrdersBySupplierId(supplierId, pageable).map(SupplierOrderFactory::toDTO));
  }
  
  @GetMapping("/automatic/{isAutomatic}")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<Page<SupplierOrderDTO>> getAutomaticSupplierOrders(
      @PathVariable("isAutomatic") boolean isAutomatic, Pageable pageable) {
    return ResponseEntity.ok(
        this.supplierOrderService.getAutomaticSupplierOrders(isAutomatic, pageable).map(SupplierOrderFactory::toDTO));
  }
  
  @GetMapping("/pending")
  @PreAuthorize("hasRole('INVENTORY')")
  public ResponseEntity<List<SupplierOrderDTO>> getPendingSupplierOrders() {
    return ResponseEntity.ok(
        this.supplierOrderService.findPendingOrders().stream()
            .map(SupplierOrderFactory::toDTO)
            .collect(Collectors.toList()));
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
