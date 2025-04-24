package com.bankly.vendura.inventory.promotion.model;

import com.bankly.vendura.inventory.product.model.Product;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "promotions")
public class Promotion {

  @Id private String id;
  @DBRef private Product product;

  private Date begin;
  private Date end;

  private double discount;

  public boolean isActive() {
    Date now = new Date();
    return (begin == null || begin.before(now)) && (end == null || end.after(now));
  }
}
