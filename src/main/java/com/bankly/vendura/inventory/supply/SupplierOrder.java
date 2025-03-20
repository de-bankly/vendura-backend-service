package com.bankly.vendura.inventory.supply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "supplier_orders")
@NoArgsConstructor
@AllArgsConstructor
public class SupplierOrder {

    @Id
    private String id;

}
