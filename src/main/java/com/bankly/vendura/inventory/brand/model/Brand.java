package com.bankly.vendura.inventory.brand.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "brands")
@NoArgsConstructor
@AllArgsConstructor
public class Brand {

  @Id private String id;

  private String name;
}
