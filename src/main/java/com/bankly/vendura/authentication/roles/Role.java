package com.bankly.vendura.authentication.roles;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role implements IRole {

    @Id
    private String id;

    private String name;

}
