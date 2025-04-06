package com.bankly.vendura.inventory.supplierorder;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrder;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderDTO;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderFactory;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderRepository;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupplierOrderService {

  private final ProductTransactionService productTransactionService;
  private final UserRepository userRepository;
  private final SupplierOrderRepository supplierOrderRepository;

  public SupplierOrder create(SupplierOrderDTO supplierOrderDTO) {
    SupplierOrder supplierOrder = SupplierOrderFactory.fromDTO(supplierOrderDTO);
    return this.supplierOrderRepository.save(supplierOrder);
  }

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

      if (supplierOrder.getOrderStatus() == SupplierOrder.OrderStatus.DELIVERED
          && supplierOrderDTO.getOrderStatus() != SupplierOrderDTO.OrderStatus.DELIVERED) {
        throw new EntityUpdateException(
            "Cannot update order status because order was already delivered",
            HttpStatus.UNPROCESSABLE_ENTITY,
            "orderStatus");
      }

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

  public void delete(String id) {
    SupplierOrder supplierOrder =
        this.supplierOrderRepository
            .findById(id)
            .orElseThrow(
                () ->
                    new EntityRetrieveException(
                        "Supplier order not found", HttpStatus.NOT_FOUND, id));

    this.supplierOrderRepository.delete(supplierOrder);
  }
}
