package com.levytech.domain;

import com.levytech.exception.StallOccupiedException;

public class Stall {
    private final int id;
    private final String code;
    private final String section;
    private final double sizeSqm;
    private Integer occupiedByTraderId;

    public Stall(int id, String code, String section, double sizeSqm, Integer occupiedByTraderId) {
        // Validation added here
        if (sizeSqm <= 0) {
            throw new IllegalArgumentException("Stall size must be positive");
        }
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Stall code is required");
        }

        this.id = id;
        this.code = code;
        this.section = section;
        this.sizeSqm = sizeSqm;
        this.occupiedByTraderId = occupiedByTraderId;
    }

    
    public int getId() { return id; }
    public String getCode() { return code; }
    public String getSection() { return section; }
    public double getSizeSqm() { return sizeSqm; }
    public Integer getOccupiedByTraderId() { return occupiedByTraderId; }

    
    public boolean isOccupied() {
        return occupiedByTraderId != null;
    }

    public void occupy(int traderId) throws StallOccupiedException {
        // Check if the stall is already occupied by a different trader
        if (isOccupied() && !occupiedByTraderId.equals(traderId)) {
            throw new StallOccupiedException(id);
        }
        this.occupiedByTraderId = traderId;
    }

    public void vacate() {
        
        this.occupiedByTraderId = null;
    }

    @Override
    public String toString() {
        return "Stall{" +
                "code='" + code + '\'' +
                ", occupiedByTraderId=" + occupiedByTraderId +
                '}';
    }
}