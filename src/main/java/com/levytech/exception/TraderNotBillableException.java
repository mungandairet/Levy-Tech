package com.levytech.exception;
public class TraderNotBillableException extends RuntimeException {
    public TraderNotBillableException(int traderId, String reason) {
        super("Trader " + traderId + " is not billable: " + reason);
    }
}