package com.bankly.vendura.inventory.transactions.product.model;

import com.bankly.vendura.authentication.user.model.User;
import com.bankly.vendura.inventory.product.model.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@Document(collection = "product_transactions")
@NoArgsConstructor
@AllArgsConstructor
public class ProductTransaction {

    @Id private String id;

    @DBRef private Product product;
    private long quantity; // positive for incoming, negative for outgoing


    private TransactionType transactionType;
    private String message;

    @DBRef private ProductTransactable transactionCause;

    @DBRef private User issuer;
    private Date timestamp;


    public enum TransactionType {
        WAREHOUSE_IN, WAREHOUSE_OUT, SALE, RETURN
    }

}
