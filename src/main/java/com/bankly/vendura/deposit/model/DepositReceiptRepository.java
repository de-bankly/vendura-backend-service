package com.bankly.vendura.deposit.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepositReceiptRepository extends MongoRepository<DepositReceipt, String> {}
