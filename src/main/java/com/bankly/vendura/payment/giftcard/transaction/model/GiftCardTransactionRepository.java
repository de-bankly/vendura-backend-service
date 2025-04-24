package com.bankly.vendura.payment.giftcard.transaction.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface GiftCardTransactionRepository
    extends MongoRepository<GiftCardTransaction, String> {}
