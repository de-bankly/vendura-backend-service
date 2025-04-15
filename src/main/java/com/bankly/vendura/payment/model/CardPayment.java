package com.bankly.vendura.payment.model;

import lombok.*;
import org.springframework.data.annotation.TypeAlias;

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

}
