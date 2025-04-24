package com.bankly.vendura.authentication.user.model;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {

  Optional<User> findUserByUsername(String username);
}
