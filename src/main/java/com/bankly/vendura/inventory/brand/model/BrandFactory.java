package com.bankly.vendura.inventory.brand.model;

import org.springframework.stereotype.Component;

@Component
public class BrandFactory {

    public static BrandDTO toDTO(Brand brand) {
        if (brand == null) return null;
        return new BrandDTO(brand.getId(), brand.getName());
    }

    public static Brand toEntity(BrandDTO brandDTO) {
        if (brandDTO == null) return null;
        return new Brand(brandDTO.getId(), brandDTO.getName());
    }

}
