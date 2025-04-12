package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.BrandDTO;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryDTO;
import com.bankly.vendura.inventory.supplier.model.SupplierDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;
    private String name;
    private ProductCategoryDTO productCategory;
    private BrandDTO brand;

    private SupplierDTO defaultSupplier;

    private Set<ProductDTO> connectedProducts;
    private Boolean standalone;

    private List<PriceHistoryDTO> priceHistories;

    @Nullable
    private Long currentStock;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceHistoryDTO {
        private Date timestamp;
        private double purchasePrice;
        private double price;
        private SupplierDTO supplier;
    }

}
