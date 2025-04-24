package com.bankly.vendura.deposit.model;

import com.bankly.vendura.inventory.product.model.ProductFactory;

import java.util.stream.Collectors;

public class DepositReceiptFactory {

    public static DepositReceipt toEntity(DepositReceiptDTO dto) {
        DepositReceipt entity = new DepositReceipt();
        entity.setId(dto.getId());
        entity.setRedeemed(dto.getRedeemed() == null ? false : dto.getRedeemed());

        for (DepositReceiptDTO.PositionDTO position : dto.getPositions()) {
      System.out.println("B" + position.getProduct().getId());
            DepositReceipt.Position entityPosition = new DepositReceipt.Position();
            entityPosition.setQuantity(position.getQuantity());
            entityPosition.setProduct(ProductFactory.toEntity(position.getProduct()));
            entity.getPositions().add(entityPosition);
        }

        return entity;
    }

    public static DepositReceiptDTO toDTO(DepositReceipt depositReceipt) {
        DepositReceiptDTO dto = new DepositReceiptDTO();
        dto.setId(depositReceipt.getId());
        dto.setTotal(depositReceipt.calculateTotal());
        dto.setRedeemed(depositReceipt.isRedeemed());
        dto.setPositions(depositReceipt.getPositions().stream().map(DepositReceiptFactory::toDTO).collect(Collectors.toSet()));
        return dto;
    }

    public static DepositReceiptDTO.PositionDTO toDTO(DepositReceipt.Position position) {
        DepositReceiptDTO.PositionDTO dto = new DepositReceiptDTO.PositionDTO();
        dto.setQuantity(position.getQuantity());
        dto.setProduct(ProductFactory.toDTO(position.getProduct()));
        return dto;
    }

}
