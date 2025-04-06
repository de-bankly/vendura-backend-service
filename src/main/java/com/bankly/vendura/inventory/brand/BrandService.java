package com.bankly.vendura.inventory.brand;

import com.bankly.vendura.inventory.brand.model.Brand;
import com.bankly.vendura.inventory.brand.model.BrandDTO;
import com.bankly.vendura.inventory.brand.model.BrandRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BrandService {

  private final BrandRepository brandRepository;

  public Brand createBrand(BrandDTO brandDTO) {

    Brand brand = new Brand();

    brand.setName(brandDTO.getName());

    return this.brandRepository.save(brand);
  }

  public Brand updateBrand(String id, BrandDTO brandDTO) {
    Brand brand =
        this.brandRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("Brand not found", HttpStatus.NOT_FOUND, id));

    if (brandDTO.getName() != null) {
      brand.setName(brandDTO.getName());
    }

    return this.brandRepository.save(brand);
  }

  public void deleteBrand(String id) {
    Brand brand =
            this.brandRepository
                    .findById(id)
                    .orElseThrow(
                            () -> new EntityRetrieveException("Brand not found", HttpStatus.NOT_FOUND, id));

    this.brandRepository.delete(brand);
  }
}
