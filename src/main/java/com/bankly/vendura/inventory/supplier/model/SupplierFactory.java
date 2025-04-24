package com.bankly.vendura.inventory.supplier.model;

public class SupplierFactory {

  public static SupplierDTO toDTO(Supplier supplier) {
    if (supplier == null) return null;
    return new SupplierDTO(
        supplier.getId(),
        supplier.getLegalName(),
        supplier.getStreet(),
        supplier.getStreetNo(),
        supplier.getCity(),
        supplier.getZip(),
        supplier.getCountry());
  }

  public static Supplier toEntity(SupplierDTO supplierDTO) {
    if (supplierDTO == null) return null;
    return new Supplier(
        supplierDTO.getId(),
        supplierDTO.getLegalName(),
        supplierDTO.getStreet(),
        supplierDTO.getStreetNo(),
        supplierDTO.getCity(),
        supplierDTO.getZip(),
        supplierDTO.getCountry());
  }
}
