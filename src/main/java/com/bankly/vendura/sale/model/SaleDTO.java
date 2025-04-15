package com.bankly.vendura.sale.model;

import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.payment.model.PaymentDTO;
import jakarta.annotation.sql.DataSourceDefinitions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleDTO {

  private String id;
  private Date date;
  private String cashierId;

  private Set<PositionDTO> positions = new HashSet<>();
  private HashSet<PaymentDTO> payments = new HashSet<>();

  private Double total;

  @Builder
  @NoArgsConstructor
  @AllArgsConstructor
  @Data
  public static class PositionDTO {

    private ProductDTO productDTO;
    private int quantity;
    private Set<PositionDTO> connectedPositions = new HashSet<>();
    private double discountEuro;

  }

}
