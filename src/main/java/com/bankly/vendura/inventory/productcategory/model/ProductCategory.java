package com.bankly.vendura.inventory.productcategory.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "product_categories")
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategory {

    @Id private String id;

    private String name;

}
