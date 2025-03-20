package com.bankly.vendura.inventory.supply;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "supplier")
@NoArgsConstructor
@AllArgsConstructor
public class Supplier {

    @Id private String id;

    private String legalName;
    private String street;
    private String streetNo;
    private String city;
    private String zip;
    private String country;

}
