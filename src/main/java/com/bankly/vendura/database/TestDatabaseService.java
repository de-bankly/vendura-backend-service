package com.bankly.vendura.database;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestDatabaseService {

  private final TestDatabaseItemRepository repository;

  @Autowired
  public TestDatabaseService(TestDatabaseItemRepository repository) {
    this.repository = repository;

    System.out.println(this.repository.count());
  }
}
