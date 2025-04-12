package com.bankly.vendura.inventory.brand.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BrandRepository extends MongoRepository<Brand, String> {

    Optional<Brand> findByName(String name);

}
