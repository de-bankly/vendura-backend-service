package com.bankly.vendura.inventory.transactions.product.model;

import com.bankly.vendura.authentication.user.model.UserDTO;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransactionDTO {
  private String id;
  private ProductDTO product;
  private long quantity;
  private String transactionType;
  private String message;
  private String transactionCauseType;
  private String transactionCauseId;
  private UserDTO issuer;
  private Date timestamp;
}
