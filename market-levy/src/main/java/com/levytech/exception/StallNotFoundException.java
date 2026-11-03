package com.levytech.exception;

public class StallNotFoundException extends RuntimeException {

    public StallNotFoundException(int stallId) {
        super("Stall not found: " + stallId);
    }
}
