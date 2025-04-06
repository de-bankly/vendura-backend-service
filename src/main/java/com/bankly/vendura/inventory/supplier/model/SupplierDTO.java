package com.bankly.vendura.inventory.supplier.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupplierDTO {

  private String id;
  private String legalName;
  private String street;
  private String streetNo;
  private String city;
  private String zip;
  private String country;
}
