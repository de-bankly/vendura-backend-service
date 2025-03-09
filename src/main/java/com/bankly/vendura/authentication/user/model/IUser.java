package com.bankly.vendura.authentication.user.model;

import com.bankly.vendura.authentication.roles.model.IRole;
import java.util.Set;

public interface IUser {

  String getId();

  String getUsername();

  String getPassword();

  <T extends IRole> Set<T> getRoles();
}
