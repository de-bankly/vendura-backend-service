package com.bankly.vendura.inventory.supplierorder.model;

import com.bankly.vendura.inventory.product.model.ProductFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierFactory;

import java.util.stream.Collectors;

public class SupplierOrderFactory {
    public static SupplierOrderDTO toDTO(SupplierOrder supplierOrder) {
        return new SupplierOrderDTO(
                supplierOrder.getId(),
                supplierOrder.getTimestamp(),
                SupplierFactory.toDTO(supplierOrder.getSupplier()),
                supplierOrder.getExpectedDeliveryDate(),
                supplierOrder.getNotes(),
                supplierOrder.isAutomaticOrder(),
                supplierOrder.getPositions().stream()
                        .map(position -> new SupplierOrderDTO.Position(
                                ProductFactory.toDTO(position.getProduct()), 
                                position.getAmount()))
                        .collect(Collectors.toSet()),
                supplierOrder.getOrderStatus().toDtoStatus()
        );
    }

    public static SupplierOrder fromDTO(SupplierOrderDTO supplierOrderDTO) {
        return new SupplierOrder(
                supplierOrderDTO.getId(),
                supplierOrderDTO.getTimestamp(),
                SupplierFactory.toEntity(supplierOrderDTO.getSupplier()),
                supplierOrderDTO.getExpectedDeliveryDate(),
                supplierOrderDTO.getNotes(),
                supplierOrderDTO.isAutomaticOrder(),
                supplierOrderDTO.getPositions().stream()
                        .map(position -> new SupplierOrder.Position(
                                ProductFactory.toEntity(position.getProduct()), 
                                position.getAmount()))
                        .collect(Collectors.toSet()),
                supplierOrderDTO.getOrderStatus().toEntityStatus()
        );
    }
}
