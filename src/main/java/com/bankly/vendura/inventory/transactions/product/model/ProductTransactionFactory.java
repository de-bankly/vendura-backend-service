package com.bankly.vendura.inventory.transactions.product.model;

import com.bankly.vendura.authentication.user.model.UserFactory;
import com.bankly.vendura.inventory.product.model.ProductFactory;

public class ProductTransactionFactory {

    public static ProductTransactionDTO toDTO(ProductTransaction transaction) {
        if (transaction == null) {
            return null;
        }

        ProductTransactionDTO dto = ProductTransactionDTO.builder()
                .id(transaction.getId())
                .product(ProductFactory.toDTO(transaction.getProduct()))
                .quantity(transaction.getQuantity())
                .transactionType(transaction.getTransactionType().name())
                .message(transaction.getMessage())
                .issuer(UserFactory.toDTO(transaction.getIssuer()))
                .timestamp(transaction.getTimestamp())
                .build();

        // Handle the transaction cause if present
        if (transaction.getTransactionCause() != null) {
            dto.setTransactionCauseType(transaction.getTransactionCause().getClass().getSimpleName());
            // Assuming the transaction cause has an ID field
            try {
                java.lang.reflect.Method getIdMethod = transaction.getTransactionCause().getClass().getMethod("getId");
                Object id = getIdMethod.invoke(transaction.getTransactionCause());
                if (id != null) {
                    dto.setTransactionCauseId(id.toString());
                }
            } catch (Exception e) {
                // Ignore if the method doesn't exist
            }
        }

        return dto;
    }
} 