package com.levytech.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LevyPlanTest {

    @Test
    public void testFlatLevyPlanCalculateCharge() {
        FlatLevyPlan plan = new FlatLevyPlan(100, 5);
        assertEquals(100, plan.calculateCharge());
    }

    @Test
    public void testTieredLevyPlanCalculateCharge() {
        TieredLevyPlan plan = new TieredLevyPlan(100, 50, 5);
        assertEquals(150, plan.calculateCharge());
    }

    @Test
    public void buildsSurchargePlan(){
        LevyPlan plan = LevyPlanFactory.fromRow("TIERED", 100, 50, 5);
        assertEquals(150, plan.calculateCharge());
        assertEquals(5, plan.getGraceDays());
    }

    @Test 
    public void rejectsUnknownPlanType(){
        assertThrows(IllegalArgumentException.class, () -> {
            LevyPlanFactory.fromRow("PREMIUM", 100, 50, 5);
        });
    }
}