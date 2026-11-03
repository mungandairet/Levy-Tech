package com.levytech.exception;

public class StallOccupiedException extends RuntimeException {
    public StallOccupiedException(int stallId) {
        super("Stall " + stallId + " is already occupied by another trader");
    }
}