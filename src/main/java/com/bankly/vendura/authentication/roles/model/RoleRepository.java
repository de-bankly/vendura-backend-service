package com.bankly.vendura.authentication.roles.model;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findRoleByName(String name);

}
