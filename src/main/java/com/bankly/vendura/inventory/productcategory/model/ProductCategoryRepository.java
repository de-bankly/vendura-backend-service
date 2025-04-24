package com.bankly.vendura.inventory.productcategory.model;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductCategoryRepository extends MongoRepository<ProductCategory, String> {

  Optional<ProductCategory> findByName(String name);
}
