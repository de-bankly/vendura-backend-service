package com.bankly.vendura.authentication.roles.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoleDTO {

    private String id;
    private String name;

    public Role toRole() {
        return new Role(id, name);
    }

    public static RoleDTO fromRole(IRole role) {
        return new RoleDTO(role.getId(), role.getName());
    }

}
