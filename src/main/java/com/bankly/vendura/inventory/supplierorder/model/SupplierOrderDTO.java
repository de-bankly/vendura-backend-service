package com.bankly.vendura.inventory.supplierorder.model;

import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.inventory.supplier.model.SupplierDTO;
import java.util.Date;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrderDTO {
  private String id;
  private Date timestamp;

  private SupplierDTO supplier;

  private Date expectedDeliveryDate;
  private String notes;
  private boolean isAutomaticOrder;

  private Set<Position> positions;
  private OrderStatus orderStatus;

  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public static class Position {
    private ProductDTO product;
    private int amount;
  }

  public enum OrderStatus {
    PLACED,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public SupplierOrder.OrderStatus toEntityStatus() {
      return SupplierOrder.OrderStatus.valueOf(name());
    }
  }
}
