package com.bankly.vendura.authentication.user;

import com.bankly.vendura.authentication.roles.IRole;
import java.util.Set;

public interface IUser {

  String getId();

  String getUsername();

  String getPassword();

  Set<? extends IRole> getRoles();
}
