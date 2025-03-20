package com.bankly.vendura.inventory.productcategory.model;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProductCategoryRepository extends MongoRepository<ProductCategory, String> {}
