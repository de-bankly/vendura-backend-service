package com.bankly.vendura.utilities.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(exception = Exception.class)
  public ResponseEntity<Map<String, Object>> handleExceptions(Exception exception) {
    HttpStatus httpStatus = this.getHttpStatus(exception);

    Map<String, Object> body = this.prefillResponse(exception);

    body.put("status", httpStatus.value());
    body.put("error", httpStatus.getReasonPhrase());

    return new ResponseEntity<>(body, httpStatus);
  }

  private Map<String, Object> prefillResponse(Exception exception) {
    LOGGER.error("Exception reached handler: {}", exception.getClass().getSimpleName());
    Map<String, Object> body = new HashMap<>();
    body.put("message", exception.getMessage());
    body.put("exception", exception.getClass().getSimpleName());
    body.put("handler", getClass().getName());

    if (exception instanceof CustomHTTPDetailedException) {
      body.putAll(((CustomHTTPDetailedException) exception).customDetails());
    }

    return body;
  }

  private HttpStatus getHttpStatus(Exception exception) {
    switch (exception) {
      case HttpStatusCodeContainer ex -> {
        return ex.getHttpStatus();
      }
      case AuthenticationException _ -> {
        return HttpStatus.UNAUTHORIZED;
      }
      case AccessDeniedException _ -> {
        return HttpStatus.FORBIDDEN;
      }
      default -> {
        return HttpStatus.BAD_REQUEST;
      }
    }
  }
}
