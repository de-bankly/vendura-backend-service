package com.bankly.vendura.inventory.product;

import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v1/inventory")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductInventoryControllerV1 {

    private final ProductStockService productStockService;
    private final ProductTransactionService productTransactionService;

    /**
     * Get all products that are low on stock
     */
    @GetMapping("/low-stock")
    @PreAuthorize("hasRole('INVENTORY')")
    public ResponseEntity<List<ProductDTO>> getLowStockProducts() {
        List<Product> products = productStockService.getProductsLowOnStock();
        
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> {
                    ProductDTO dto = ProductFactory.toDTO(product);
                    dto.setCurrentStock(productTransactionService.calculateCurrentStock(product.getId()));
                    return dto;
                })
                .collect(Collectors.toList());
                
        return ResponseEntity.ok(productDTOs);
    }
    
    /**
     * Adjust stock level for a product
     */
    @PostMapping("/{productId}/adjust")
    @PreAuthorize("hasRole('INVENTORY')")
    public ResponseEntity<?> adjustStock(
            @PathVariable("productId") String productId,
            @RequestParam("quantity") long quantity,
            @RequestParam(value = "reason", required = false) String reason,
            Authentication authentication) {
            
        productStockService.adjustStock(
                productId,
                quantity,
                authentication.getName(),
                reason
        );
        
        return ResponseEntity.ok().build();
    }
    
    /**
     * Trigger automatic reordering check
     */
    @PostMapping("/reorder-check")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> triggerReorderCheck() {
        productStockService.checkStockLevelsAndReorder();
        return ResponseEntity.ok().build();
    }
} 