package com.bankly.vendura.database;

import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * This repository is for the purpose of demonstration Database Repositories are needed for handling
 * a specific objects CRUD operations Every database object needs a Repository, extended by the
 * interface MongoRepository with the generic types of the objects type and the type of the primary
 * key
 */
public interface TestDatabaseItemRepository extends MongoRepository<TestDatabaseItem, String> {

  TestDatabaseItem findTestDatabaseItemByName(String name);
  // these statements do magical things - operations are defined by the name of the
  // abstract method

  @Override
  long count();
}
