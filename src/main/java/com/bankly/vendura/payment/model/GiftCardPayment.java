package com.bankly.vendura.payment.model;

import com.bankly.vendura.payment.giftcard.model.GiftCard;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;

@Getter
@Setter
@TypeAlias("giftCardPayment")
public class GiftCardPayment extends Payment {

    @DBRef private GiftCard giftCard;

}
