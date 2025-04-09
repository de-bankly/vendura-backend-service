package com.bankly.vendura.inventory.transactions.product.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ProductTransactionRepository extends MongoRepository<ProductTransaction, String> {
    
    /**
     * Find all transactions for a product
     * @param productId The product ID
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    @Query("{ 'product._id': ?0 }")
    Page<ProductTransaction> findByProductIdOrderByTimestampDesc(String productId, Pageable pageable);
    
    /**
     * Find all transactions ordered by timestamp
     * @param pageable Pagination parameters
     * @return Page of transactions
     */
    Page<ProductTransaction> findAllByOrderByTimestampDesc(Pageable pageable);
}
