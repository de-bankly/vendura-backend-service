package com.bankly.vendura.deposit.model;

import com.bankly.vendura.inventory.product.model.ProductFactory;

public class DepositReceiptFactory {

    public static DepositReceipt toEntity(DepositReceiptDTO dto) {
        DepositReceipt entity = new DepositReceipt();
        entity.setId(dto.getId());

        for (DepositReceiptDTO.PositionDTO position : dto.getPositions()) {
            DepositReceipt.Position entityPosition = new DepositReceipt.Position();
            entityPosition.setQuantity(position.getQuantity());
            entityPosition.setProduct(ProductFactory.toEntity(position.getProductDTO()));
            entity.getPositions().add(entityPosition);
        }

        return new DepositReceipt();
    }

    public static DepositReceiptDTO toDTO(DepositReceipt depositReceipt) {
        // todo
        return null;
    }

}
