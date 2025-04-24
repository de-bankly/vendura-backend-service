package com.bankly.vendura.sale.model;

import com.bankly.vendura.inventory.product.model.ProductDTO;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingProductsDTO {

  private int limit;
  private Map<String, Integer> topSellingProducts;
  private List<ProductDTO> bulkFetchProducts;
}
