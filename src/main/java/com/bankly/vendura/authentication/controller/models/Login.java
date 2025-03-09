package com.bankly.vendura.authentication.controller.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class Login {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String username;
        private String password;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private String token;
        private String username;
        private List<String> roles;
    }

}
