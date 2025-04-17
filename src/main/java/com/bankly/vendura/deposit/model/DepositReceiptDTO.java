package com.bankly.vendura.deposit.model;

import com.bankly.vendura.inventory.product.model.ProductDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositReceiptDTO {

    private String id;
    private Set<PositionDTO> positions;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PositionDTO {
        private int quantity;
        private ProductDTO productDTO;
    }

}
