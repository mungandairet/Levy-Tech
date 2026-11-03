package com.levytech.domain;

import com.levytech.exception.StallOccupiedException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StallTest {

    @Test
    public void testOccupyAndVacate() {
        Stall stall = new Stall(1, "A-01", "Section A", 6.0, null);
        
        // Initially vacant
        assertFalse(stall.isOccupied());
        assertNull(stall.getOccupiedByTraderId());

        // Occupy it
        stall.occupy(99);
        assertTrue(stall.isOccupied());
        assertEquals(99, stall.getOccupiedByTraderId());

        // Vacate it
        stall.vacate();
        assertFalse(stall.isOccupied());
        assertNull(stall.getOccupiedByTraderId());
    }

    @Test
    public void testCannotOccupyOccupiedStall() {
        // Stall is already occupied by trader 99
        Stall stall = new Stall(1, "A-01", "Section A", 6.0, 99);

        // Try to put trader 100 in it
        assertThrows(StallOccupiedException.class, () -> {
            stall.occupy(100);
        });

        // Trader 99 should still be in the stall (unchanged)
        assertEquals(99, stall.getOccupiedByTraderId());
    }

    @Test
    public void testCanReOccupyOwnStall() {
        // Stall is occupied by trader 99
        Stall stall = new Stall(1, "A-01", "Section A", 6.0, 99);

        // Trader 99 tries to occupy it again (should NOT throw an error)
        assertDoesNotThrow(() -> {
            stall.occupy(99);
        });
        assertEquals(99, stall.getOccupiedByTraderId());
    }

    @Test
    public void testInvalidStallCreation() {
        
        assertThrows(IllegalArgumentException.class, () -> {
            new Stall(1, "A-01", "Section A", -5.0, null);
        });

        // Cannot create a stall with a blank code
        assertThrows(IllegalArgumentException.class, () -> {
            new Stall(1, "  ", "Section A", 6.0, null);
        });
    }
}