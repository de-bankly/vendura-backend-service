package com.bankly.vendura.payment.model;

import com.bankly.vendura.authentication.user.model.User;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("cardPayment")
public class CardPayment extends Payment {

    private String cardNumber;
    private String cardHolderName;
    private String expirationDate;
    private String cvv;

    @Override
    public int getPaymentHierarchy() {
        return 1;
    }

    public CardPayment(Date timestamp, double amount, User issuer, Status status, String cardNumber, String cardHolderName, String expirationDate, String cvv) {
        super(null, timestamp, amount, issuer, status);
        this.cardNumber = cardNumber;
        this.cardHolderName = cardHolderName;
        this.expirationDate = expirationDate;
        this.cvv = cvv;
    }
}
