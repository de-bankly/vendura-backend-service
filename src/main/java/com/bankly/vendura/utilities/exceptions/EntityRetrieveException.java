package com.bankly.vendura.utilities.exceptions;

import java.util.Map;
import lombok.Getter;
import org.springframework.http.HttpStatus;

public class EntityRetrieveException extends RuntimeException
    implements HttpStatusCodeContainer, CustomHTTPDetailedException {

  @Getter private final HttpStatus httpStatus;

  private final String id;

  public EntityRetrieveException(String message, HttpStatus httpStatus, String id) {
    super(message);
    this.httpStatus = httpStatus;
    this.id = id;
  }

  @Override
  public Map<String, Object> customDetails() {
    return Map.of("id", this.id);
  }
}
