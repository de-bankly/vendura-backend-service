package com.bankly.vendura.payment.model;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Date;
import java.util.List;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    @Query("{ 'timestamp': { $lt: ?0, $gt: ?1 } }")
    List<Payment> findAllByTimestampBeforeAndTimestampAfter(Date before, Date after);

}
