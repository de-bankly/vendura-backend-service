package com.bankly.vendura.deposit.model;

import com.bankly.vendura.inventory.product.model.ProductDTO;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepositReceiptDTO {

    @Null(message = "ID cannot be set upon creation")
    private String id;

    @NotNull(message = "Positions cannot be null")
    private Set<PositionDTO> positions;

    @Null(message = "Redeemed status only has informative meaning")
    private Boolean redeemed;

    @Null(message = "Redeemed status only has informative meaning")
    private Double total;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PositionDTO {
        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
        @NotNull(message = "Product cannot be null")
        private ProductDTO product;
    }

}
