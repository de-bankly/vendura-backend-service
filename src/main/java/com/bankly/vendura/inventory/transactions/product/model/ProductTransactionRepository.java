package com.bankly.vendura.inventory.transactions.product.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductTransactionRepository extends MongoRepository<ProductTransaction, String> {}
