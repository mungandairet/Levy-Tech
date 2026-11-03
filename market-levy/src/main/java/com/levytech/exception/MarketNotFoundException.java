package com.levytech.exception;

public class MarketNotFoundException extends RuntimeException {

    public MarketNotFoundException(int marketId) {
        super("Market not found: " + marketId);
    }
}
