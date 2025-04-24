package com.bankly.vendura.utilities.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomAuthenticationException extends RuntimeException
    implements HttpStatusCodeContainer {

  private final HttpStatus httpStatus;

  public CustomAuthenticationException(String message, Throwable cause, HttpStatus httpStatus) {
    super(message, cause);
    this.httpStatus = httpStatus;
  }
}
