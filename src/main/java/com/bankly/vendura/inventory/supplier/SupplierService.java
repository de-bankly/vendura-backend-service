package com.bankly.vendura.inventory.supplier;

import com.bankly.vendura.inventory.supplier.model.Supplier;
import com.bankly.vendura.inventory.supplier.model.SupplierDTO;
import com.bankly.vendura.inventory.supplier.model.SupplierFactory;
import com.bankly.vendura.inventory.supplier.model.SupplierRepository;
import com.bankly.vendura.utilities.exceptions.EntityRetrieveException;
import com.bankly.vendura.utilities.exceptions.EntityUpdateException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SupplierService {

  private final SupplierRepository supplierRepository;

  public Supplier createSupplier(SupplierDTO supplierDTO) {
    Supplier supplier = SupplierFactory.toEntity(supplierDTO);

    if (supplier.getLegalName() == null
        || supplier.getStreet() == null
        || supplier.getStreetNo() == null
        || supplier.getCity() == null
        || supplier.getZip() == null
        || supplier.getCountry() == null) {
        throw new EntityCreationException("All fields are required", HttpStatus.UNPROCESSABLE_ENTITY, "supplierDTO", true);
    }

        return this.supplierRepository.save(supplier);
  }

  public Supplier updateSupplier(String id, SupplierDTO supplierDTO) {
    Supplier supplier =
        this.supplierRepository
            .findById(id)
            .orElseThrow(
                () -> new EntityRetrieveException("Supplier not found", HttpStatus.NOT_FOUND, id));

    if (supplierDTO.getId() != null) {
      throw new EntityUpdateException(
          "Cannot update suppliers ID", HttpStatus.UNPROCESSABLE_ENTITY, "id");
    }

    if (supplierDTO.getLegalName() != null
        && !supplier.getLegalName().equals(supplierDTO.getLegalName())) {
      supplier.setLegalName(supplierDTO.getLegalName());
    }

    if (supplierDTO.getStreet() != null && !supplier.getStreet().equals(supplierDTO.getStreet())) {
      supplier.setStreet(supplierDTO.getStreet());
    }

    if (supplierDTO.getStreetNo() != null
        && !supplier.getStreetNo().equals(supplierDTO.getStreetNo())) {
      supplier.setStreetNo(supplierDTO.getStreetNo());
    }

    if (supplierDTO.getCity() != null && !supplier.getCity().equals(supplierDTO.getCity())) {
      supplier.setCity(supplierDTO.getCity());
    }

    if (supplierDTO.getZip() != null && !supplier.getZip().equals(supplierDTO.getZip())) {
      supplier.setZip(supplierDTO.getZip());
    }

    if (supplierDTO.getCountry() != null
        && !supplier.getCountry().equals(supplierDTO.getCountry())) {
      supplier.setCountry(supplierDTO.getCountry());
    }

    return this.supplierRepository.save(supplier);
  }
}
