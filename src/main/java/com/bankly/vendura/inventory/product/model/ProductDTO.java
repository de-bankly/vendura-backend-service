package com.bankly.vendura.inventory.product.model;

import com.bankly.vendura.inventory.brand.model.BrandDTO;
import com.bankly.vendura.inventory.productcategory.model.ProductCategoryDTO;
import com.bankly.vendura.inventory.supplier.model.SupplierDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    private String id;
    private String name;
    private String shortDescription;
    private String longDescription;
    private ProductCategoryDTO productCategory;
    private BrandDTO brand;

    private SupplierDTO defaultSupplier;
    
    private Long minStockLevel;
    private Long maxStockLevel;
    private Long reorderPoint;
    private Long reorderQuantity;
    private Long leadTimeInDays;
    
    private Set<ProductDTO> connectedProducts;
    private Boolean standalone;

    private List<PriceHistoryDTO> priceHistories;

    @Nullable
    private Long currentStock;

    /**
     * Gets the current price from the latest price history entry
     * @return the current price or 0 if no price history exists
     */
    public double getPrice() {
        if (priceHistories == null || priceHistories.isEmpty()) {
            return 0;
        }
        return priceHistories.stream()
            .max(Comparator.comparing(PriceHistoryDTO::getTimestamp))
            .map(PriceHistoryDTO::getPrice)
            .orElse(0.0);
    }

    /**
     * Gets the current purchase price from the latest price history entry
     * @return the current purchase price or 0 if no price history exists
     */
    public double getPurchasePrice() {
        if (priceHistories == null || priceHistories.isEmpty()) {
            return 0;
        }
        return priceHistories.stream()
            .max(Comparator.comparing(PriceHistoryDTO::getTimestamp))
            .map(PriceHistoryDTO::getPurchasePrice)
            .orElse(0.0);
    }

    /**
     * Gets the price at a specific point in time
     * @param date The date to check for price
     * @return The price at the given date or latest price before that date
     */
    public double getPriceAtDate(Date date) {
        if (priceHistories == null || priceHistories.isEmpty()) {
            return 0;
        }
        return priceHistories.stream()
            .filter(ph -> ph.getTimestamp().before(date) || ph.getTimestamp().equals(date))
            .max(Comparator.comparing(PriceHistoryDTO::getTimestamp))
            .map(PriceHistoryDTO::getPrice)
            .orElse(0.0);
    }

    /**
     * Gets the purchase price at a specific point in time
     * @param date The date to check for purchase price
     * @return The purchase price at the given date or latest price before that date
     */
    public double getPurchasePriceAtDate(Date date) {
        if (priceHistories == null || priceHistories.isEmpty()) {
            return 0;
        }
        return priceHistories.stream()
            .filter(ph -> ph.getTimestamp().before(date) || ph.getTimestamp().equals(date))
            .max(Comparator.comparing(PriceHistoryDTO::getTimestamp))
            .map(PriceHistoryDTO::getPurchasePrice)
            .orElse(0.0);
    }

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
