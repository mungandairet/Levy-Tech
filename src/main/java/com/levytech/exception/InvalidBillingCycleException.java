package com.levytech.exception;
public class InvalidBillingCycleException extends RuntimeException {
    public InvalidBillingCycleException(String cycle) {
        super("Invalid billing cycle: " + cycle);
    }
}