package com.bankly.vendura.deposit.model;

import com.bankly.vendura.inventory.product.model.Product;
import com.bankly.vendura.inventory.product.model.ProductDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DepositReceiptMapper {

    DepositReceipt toEntity(DepositReceiptDTO depositReceiptDTO);
    DepositReceiptDTO toDTO(DepositReceipt depositReceipt);

    Product toEntity(ProductDTO productDTO);
    ProductDTO toDTO(Product product);

}
