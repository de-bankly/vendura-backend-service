package com.bankly.vendura.utilities.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class EntityCreationException extends RuntimeException implements HttpStatusCodeContainer {

  private final HttpStatus httpStatus;
  private final String entity;
  private final boolean transactional;

  public EntityCreationException(
      String message, HttpStatus httpStatus, String entity, boolean transactional) {
    super(message);
    this.httpStatus = httpStatus;
    this.entity = entity;
    this.transactional = transactional;
  }

  public EntityCreationException(String message, HttpStatus httpStatus) {
    super(message);
    this.httpStatus = httpStatus;
    this.entity = null;
    this.transactional = false;
  }
}
