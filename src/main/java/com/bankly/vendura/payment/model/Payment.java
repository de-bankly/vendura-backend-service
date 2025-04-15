package com.bankly.vendura.payment.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@TypeAlias("payment")
@Document(collection = "payments")
public class Payment {

    @Id private String id;
    private Date timestamp;
    private double amount;

}
