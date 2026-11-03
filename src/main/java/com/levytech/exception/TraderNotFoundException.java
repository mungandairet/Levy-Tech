package com.levytech.exception;

public class TraderNotFoundException extends RuntimeException {
    public TraderNotFoundException(int traderId) {
        super("Trader not found: " + traderId);
    }
}