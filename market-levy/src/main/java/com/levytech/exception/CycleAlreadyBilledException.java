package com.levytech.exception;
public class CycleAlreadyBilledException extends RuntimeException {
    public CycleAlreadyBilledException(int traderId, String cycle) {
        super("Trader " + traderId + " is already billed for cycle " + cycle);
    }
}