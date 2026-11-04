package com.levytech.domain;

public record Market(int id, String name, String location) {

    public Market {
        if (id < 0) {
            throw new IllegalArgumentException("Market ID cannot be negative");
        }

        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Market name is required");
        }

        if (location == null || location.isBlank()) {
            throw new IllegalArgumentException("Market location is required");
        }

        name = name.trim();
        location = location.trim();
    }
}
