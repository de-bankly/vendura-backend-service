package com.bankly.vendura.authentication.roles.model;

import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RoleRepository extends MongoRepository<Role, String> {

  Optional<Role> findRoleByName(String name);
}
