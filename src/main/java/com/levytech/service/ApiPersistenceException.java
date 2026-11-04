package com.levytech.service;

import java.sql.SQLException;

public class ApiPersistenceException extends RuntimeException {

    public ApiPersistenceException(String message, SQLException cause) {
        super(message, cause);
    }
}
