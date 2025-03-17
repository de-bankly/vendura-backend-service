package com.bankly.vendura.utilities.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public class EntityUpdateException extends RuntimeException implements HttpStatusCodeContainer, CustomHTTPDetailedException {

  public EntityUpdateException(String message, HttpStatus httpStatus, String attributeFailure) {
    super(message);
    this.httpStatus = httpStatus;
    this.attributeFailure = attributeFailure;
  }

  @Getter
  private final HttpStatus httpStatus;
  private final String attributeFailure;

  @Override
  public Map<String, Object> customDetails() {
    return Map.of("attribute", this.attributeFailure);
  }
}
