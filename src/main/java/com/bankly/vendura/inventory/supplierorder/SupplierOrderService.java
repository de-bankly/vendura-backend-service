package com.bankly.vendura.inventory.supplierorder;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierRepository;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrder;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderDTO;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderFactory;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderRepository;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import java.util.Date;
import java.util.List;
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
public class SupplierOrderService {

  private final ProductTransactionService productTransactionService;
  private final UserRepository userRepository;
  private final SupplierOrderRepository supplierOrderRepository;
  private final SupplierRepository supplierRepository;

  /**
   * Create a new supplier order
   */
  @Transactional
  public SupplierOrder create(SupplierOrderDTO supplierOrderDTO) {
    // Set default values if not provided
    if (supplierOrderDTO.getTimestamp() == null) {
      supplierOrderDTO.setTimestamp(new Date());
    }
    
    if (supplierOrderDTO.getOrderStatus() == null) {
      supplierOrderDTO.setOrderStatus(SupplierOrderDTO.OrderStatus.PLACED);
    }
    
    if (supplierOrderDTO.getPurchaseOrderNumber() == null || supplierOrderDTO.getPurchaseOrderNumber().isEmpty()) {
      supplierOrderDTO.setPurchaseOrderNumber(generatePurchaseOrderNumber());
    }
    
    SupplierOrder supplierOrder = SupplierOrderFactory.fromDTO(supplierOrderDTO);
    return this.supplierOrderRepository.save(supplierOrder);
  }

  /**
   * Generate a unique purchase order number
   */
  private String generatePurchaseOrderNumber() {
    return "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
  }

  /**
   * Get supplier orders by status
   */
  public Page<SupplierOrder> getSupplierOrdersByStatus(SupplierOrder.OrderStatus status, Pageable pageable) {
    return supplierOrderRepository.findByOrderStatus(status, pageable);
  }

  /**
   * Find pending supplier orders
   */
  public List<SupplierOrder> findPendingOrders() {
    return supplierOrderRepository.findByOrderStatusIn(
        List.of(SupplierOrder.OrderStatus.PLACED, SupplierOrder.OrderStatus.SHIPPED));
  }

  /**
   * Update a supplier order
   */
  @Transactional
  public SupplierOrder update(String id, SupplierOrderDTO supplierOrderDTO, String username) {
    SupplierOrder supplierOrder =
        this.supplierOrderRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Supplier order not found", HttpStatus.NOT_FOUND, id));

    if (supplierOrderDTO.getId() != null) {
      throw new EntityUpdateException("Cannot update ID", HttpStatus.UNPROCESSABLE_ENTITY, "id");
    }

    if (supplierOrderDTO.getTimestamp() != null) {
      throw new EntityUpdateException(
          "Cannot update timestamp", HttpStatus.UNPROCESSABLE_ENTITY, "timestamp");
    }
    
    // Update supplier if provided
    if (supplierOrderDTO.getSupplier() != null && supplierOrderDTO.getSupplier().getId() != null) {
      supplierOrder.setSupplier(
          supplierRepository
              .findById(supplierOrderDTO.getSupplier().getId())
              .orElseThrow(
                  () ->
                      new EntityRetrieveException(
                          "Supplier not found", 
                          HttpStatus.NOT_FOUND, 
                          supplierOrderDTO.getSupplier().getId())));
    }
    
    // Update other fields if provided
    if (supplierOrderDTO.getPurchaseOrderNumber() != null) {
      supplierOrder.setPurchaseOrderNumber(supplierOrderDTO.getPurchaseOrderNumber());
    }
    
    if (supplierOrderDTO.getExpectedDeliveryDate() != null) {
      supplierOrder.setExpectedDeliveryDate(supplierOrderDTO.getExpectedDeliveryDate());
    }
    
    if (supplierOrderDTO.getNotes() != null) {
      supplierOrder.setNotes(supplierOrderDTO.getNotes());
    }

    if (supplierOrderDTO.getPositions() != null) {
      supplierOrder.setPositions(
          supplierOrderDTO.getPositions().stream()
              .map(
                  positionDTO ->
                      new SupplierOrder.Position(
                          ProductFactory.toEntity(positionDTO.getProduct()),
                          positionDTO.getAmount()))
              .collect(Collectors.toSet()));
    }

    if (supplierOrderDTO.getOrderStatus() != null) {
      // Cannot change status from DELIVERED to something else
      if (supplierOrder.getOrderStatus() == SupplierOrder.OrderStatus.DELIVERED
          && supplierOrderDTO.getOrderStatus() != SupplierOrderDTO.OrderStatus.DELIVERED) {
        throw new EntityUpdateException(
            "Cannot update order status because order was already delivered",
            HttpStatus.UNPROCESSABLE_ENTITY,
            "orderStatus");
      }

      // When status changes to DELIVERED, create inventory transactions
      if (supplierOrderDTO.getOrderStatus() == SupplierOrderDTO.OrderStatus.DELIVERED
          && supplierOrder.getOrderStatus() != SupplierOrder.OrderStatus.DELIVERED) {

        User user = this.userRepository.findUserByUsername(username).orElseThrow();

        for (SupplierOrder.Position position : supplierOrder.getPositions()) {
          this.productTransactionService.createTransaction(
              position.getProduct(),
              position.getAmount(),
              supplierOrder,
              user,
              "Automatic entry in the warehouse because the status of the supplier order has been changed to DELIVERED");
        }
      }

      supplierOrder.setOrderStatus(supplierOrderDTO.getOrderStatus().toEntityStatus());
    }

    return this.supplierOrderRepository.save(supplierOrder);
  }

  /**
   * Delete a supplier order
   */
  @Transactional
  public void delete(String id) {
    SupplierOrder supplierOrder =
        this.supplierOrderRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Supplier order not found", HttpStatus.NOT_FOUND, id));
                        
    // Can only delete orders that are not DELIVERED
    if (supplierOrder.getOrderStatus() == SupplierOrder.OrderStatus.DELIVERED) {
      throw new EntityUpdateException(
          "Cannot delete a delivered order",
          HttpStatus.UNPROCESSABLE_ENTITY,
          "orderStatus");
    }

    this.supplierOrderRepository.delete(supplierOrder);
  }
}
