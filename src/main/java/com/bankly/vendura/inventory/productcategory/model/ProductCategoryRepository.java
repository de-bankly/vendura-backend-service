package com.bankly.vendura.inventory.productcategory.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ProductCategoryRepository extends MongoRepository<ProductCategory, String> {

    Optional<ProductCategory> findByName(String name);

}
