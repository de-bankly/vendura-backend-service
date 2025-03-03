package com.bankly.vendura.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EntityCreationExceptionResponse {

    private String path;
    private String error;
    private String message;
    private int status;

}
