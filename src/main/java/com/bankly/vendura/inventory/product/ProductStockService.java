package com.bankly.vendura.inventory.product;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.authentication.user.model.UserRepository;
import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductRepository;
import com.bankly.vendura.inventory.supplier.model.Supplier;
import com.bankly.vendura.inventory.supplierorder.SupplierOrderService;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrder;
import com.bankly.vendura.inventory.supplierorder.model.SupplierOrderDTO;
import com.bankly.vendura.inventory.transactions.product.ProductTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ProductStockService {

    private final ProductRepository productRepository;
    private final ProductTransactionService transactionService;
    private final SupplierOrderService supplierOrderService;
    private final UserRepository userRepository;

    /**
     * Check stock levels for all products and create supplier orders if needed
     */
    @Transactional
    @Scheduled(cron = "0 0 * * * *") // Run once per hour
    public void checkStockLevelsAndReorder() {
        // Group products by supplier that need reordering
        Map<Supplier, List<Product>> productsToReorder = new HashMap<>();
        
        for (Product product : productRepository.findAll()) {
            if (product.getDefaultSupplier() == null || product.getReorderPoint() == null) {
                continue; // Skip products without supplier or reorder point
            }
            
            long currentStock = transactionService.calculateCurrentStock(product);
            
            // Check if stock is below reorder point
            if (currentStock <= product.getReorderPoint()) {
                productsToReorder
                    .computeIfAbsent(product.getDefaultSupplier(), k -> new ArrayList<>())
                    .add(product);
            }
        }
        
        // Create supplier orders for each supplier
        for (Map.Entry<Supplier, List<Product>> entry : productsToReorder.entrySet()) {
            createSupplierOrderForProducts(entry.getKey(), entry.getValue());
        }
    }
    
    /**
     * Create a supplier order for a list of products
     */
    private void createSupplierOrderForProducts(Supplier supplier, List<Product> products) {
        if (products.isEmpty()) {
            return;
        }
        
        // Create supplier order positions
        Set<SupplierOrderDTO.Position> positions = new HashSet<>();
        for (Product product : products) {
            long currentStock = transactionService.calculateCurrentStock(product);
            long quantityToOrder = product.getReorderQuantity() != null ? 
                    product.getReorderQuantity() : 
                    (product.getMaxStockLevel() != null ? 
                            product.getMaxStockLevel() - currentStock : 10);
                            
            SupplierOrderDTO.Position position = new SupplierOrderDTO.Position();
            position.setProduct(com.bankly.vendura.inventory.product.model.ProductFactory.toDTO(product));
            position.setAmount((int) quantityToOrder);
            positions.add(position);
        }
        
        // Create the supplier order
        SupplierOrderDTO orderDTO = new SupplierOrderDTO();
        orderDTO.setTimestamp(new Date());
        orderDTO.setPositions(positions);
        orderDTO.setOrderStatus(SupplierOrderDTO.OrderStatus.PLACED);
        
        supplierOrderService.create(orderDTO);
    }
    
    /**
     * Check if product is low on stock
     */
    public boolean isLowOnStock(Product product) {
        if (product.getMinStockLevel() == null) {
            return false;
        }
        
        long currentStock = transactionService.calculateCurrentStock(product);
        return currentStock <= product.getMinStockLevel();
    }
    
    /**
     * Get all products that are low on stock
     */
    public List<Product> getProductsLowOnStock() {
        List<Product> lowStockProducts = new ArrayList<>();
        
        for (Product product : productRepository.findAll()) {
            if (isLowOnStock(product)) {
                lowStockProducts.add(product);
            }
        }
        
        return lowStockProducts;
    }
    
    /**
     * Record stock adjustment (manual inventory correction)
     */
    @Transactional
    public void adjustStock(String productId, long quantityChange, String username, String reason) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        transactionService.createTransaction(
                product,
                quantityChange,
                null, // No transaction cause for manual adjustments
                user,
                reason != null ? reason : "Manual stock adjustment"
        );
    }
} 