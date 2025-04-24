package com.bankly.vendura.inventory.brand.model;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface BrandRepository extends MongoRepository<Brand, String> {

  Optional<Brand> findByName(String name);
}
