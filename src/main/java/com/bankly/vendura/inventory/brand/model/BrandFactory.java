package com.bankly.vendura.inventory.brand.model;

import org.springframework.stereotype.Component;

@Component
public class BrandFactory {

    public static BrandDTO toDTO(Brand brand) {
        return new BrandDTO(brand.getId(), brand.getName());
    }

    public static Brand toEntity(BrandDTO brandDTO) {
        return new Brand(brandDTO.getId(), brandDTO.getName());
    }

}
