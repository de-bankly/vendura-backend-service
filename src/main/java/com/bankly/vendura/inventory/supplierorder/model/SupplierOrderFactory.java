package com.bankly.vendura.inventory.supplierorder.model;

import com.bankly.vendura.inventory.product.model.ProductFactory;

import java.util.stream.Collectors;

public class SupplierOrderFactory {
    public static SupplierOrderDTO toDTO(SupplierOrder supplierOrder) {
        return new SupplierOrderDTO(
                supplierOrder.getId(),
                supplierOrder.getTimestamp(),
                supplierOrder.getPositions().stream()
                        .map(position -> new SupplierOrderDTO.Position(ProductFactory.toDTO(position.getProduct()), position.getAmount()))
                        .collect(Collectors.toSet()),
                supplierOrder.getOrderStatus().toDtoStatus()
        );
    }

    public static SupplierOrder fromDTO(SupplierOrderDTO supplierOrderDTO) {
        return new SupplierOrder(
                supplierOrderDTO.getId(),
                supplierOrderDTO.getTimestamp(),
                supplierOrderDTO.getPositions().stream()
                        .map(position -> new SupplierOrder.Position(ProductFactory.toEntity(position.getProduct()), position.getAmount()))
                        .collect(Collectors.toSet()),
                supplierOrderDTO.getOrderStatus().toEntityStatus()
        );
    }
}
