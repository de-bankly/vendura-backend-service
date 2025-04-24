package com.bankly.vendura.sale.model;

import com.bankly.vendura.deposit.model.DepositReceiptDTO;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.payment.model.PaymentDTO;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SaleDTO {

  private String id;
  private Date date;
  private String cashierId;

  private Set<PositionDTO> positions = new HashSet<>();
  private HashSet<PaymentDTO> payments = new HashSet<>();
  private Set<DepositReceiptDTO> depositReceipts = new HashSet<>();

  private Double total;

  @NoArgsConstructor
  @AllArgsConstructor
  @Builder(toBuilder = true)
  @Data
  public static class PositionDTO {

    private ProductDTO productDTO;
    private int quantity;
    private Set<PositionDTO> connectedPositions = new HashSet<>();
    private double discountEuro;

    public static PositionDTOBuilder builder() {
      return new PositionDTO().toBuilder();
    }
  }

  public static SaleDTOBuilder builder() {
    return new SaleDTOBuilder();
  }

  public static class SaleDTOBuilder {
    private final SaleDTO instance;

    public SaleDTOBuilder() {
      this.instance = new SaleDTO();
    }

    public SaleDTOBuilder id(String id) {
      this.instance.setId(id);
      return this;
    }

    public SaleDTOBuilder date(Date date) {
      this.instance.setDate(date);
      return this;
    }

    public SaleDTOBuilder cashierId(String cashierId) {
      this.instance.setCashierId(cashierId);
      return this;
    }

    public SaleDTOBuilder positions(Set<PositionDTO> positions) {
      this.instance.setPositions(positions);
      return this;
    }

    public SaleDTOBuilder payments(HashSet<PaymentDTO> payments) {
      this.instance.setPayments(payments);
      return this;
    }

    public SaleDTOBuilder depositReceipts(Set<DepositReceiptDTO> depositReceipts) {
      this.instance.setDepositReceipts(depositReceipts);
      return this;
    }

    public SaleDTOBuilder total(Double total) {
      this.instance.setTotal(total);
      return this;
    }

    public SaleDTO build() {
      return this.instance;
    }
  }
}
