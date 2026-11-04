package com.levytech.exception;

public class PlanNotFoundException extends RuntimeException {

    public PlanNotFoundException(int planId) {
        super("Levy plan not found: " + planId);
    }
}
