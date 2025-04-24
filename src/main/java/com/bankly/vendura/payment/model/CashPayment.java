package com.bankly.vendura.payment.model;

import com.bankly.vendura.authentication.user.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@TypeAlias("cashPayment")
public class CashPayment extends Payment {

    private double handed;
    private double returned;

    @Override
    public int getPaymentHierarchy() {
        return 2;
    }

    public CashPayment(Date timestamp, double amount, User issuer, double handed, Status status) {
        super(null, timestamp, amount, issuer, status);
        this.handed = handed;
    }
}
