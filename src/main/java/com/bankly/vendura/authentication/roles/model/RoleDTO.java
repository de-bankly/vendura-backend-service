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
    private Boolean active;

    public Role toRole() {
        return new Role(id, name, active);
    }

    public static RoleDTO fromRole(Role role) {
        return new RoleDTO(role.getId(), role.getName(), role.isActive());
    }

}
