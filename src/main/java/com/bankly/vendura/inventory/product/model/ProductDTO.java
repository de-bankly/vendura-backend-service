package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.BrandDTO;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;
    private String name;
    private ProductCategoryDTO productCategory;
    private BrandDTO brand;

    private SupplierDTO supplier;
    private List<PriceHistoryDTO> priceHistories;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PriceHistoryDTO {
        private long timestamp;
        private double purchasePrice;
        private double price;
        private SupplierDTO supplier;
    }

}
