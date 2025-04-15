package com.bankly.vendura.payment.model;

import com.bankly.vendura.authentication.user.model.User;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;

@TypeAlias("cashPayment")
public class CashPayment extends Payment {

    @DBRef private User responsibleUser;

}
