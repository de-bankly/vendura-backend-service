package com.bankly.vendura.payment.model;

import com.bankly.vendura.authentication.user.model.User;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TypeAlias("payment")
@Document(collection = "payments")
public abstract class Payment {

    public abstract int getPaymentHierarchy();

    @Id private String id;
    private Date timestamp;
    private double amount;
    @DBRef private User issuer;
    private Status status;

    public enum Status {
        PENDING,
        COMPLETED,
        FAILED,
        REVERTED;

        public PaymentDTO.Status toDTOStatus() {
            return PaymentDTO.Status.valueOf(this.name());
        }
    }

    public Payment(Date timestamp, double amount, User issuer) {
        this.timestamp = timestamp;
        this.amount = amount;
        this.issuer = issuer;
        this.status = Status.PENDING;
    }
}
