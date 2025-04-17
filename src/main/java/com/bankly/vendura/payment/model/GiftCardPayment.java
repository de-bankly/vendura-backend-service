package com.bankly.vendura.payment.model;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.payment.giftcard.model.GiftCard;
import com.bankly.vendura.payment.giftcard.transaction.model.GiftcardTransactable;
import lombok.*;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("giftCardPayment")
public class GiftCardPayment extends Payment implements GiftcardTransactable {

    @DBRef private GiftCard giftCard;

    @Override
    public int getPaymentHierarchy() {
        return 100;
    }

    public GiftCardPayment(Date timestamp, double amount, User issuer, Status status, GiftCard giftCard) {
        super(null, timestamp, amount, issuer, status);
        this.giftCard = giftCard;
    }

}
