package com.levytech.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class LevyPlanTest {

    @Test
    public void testFlatLevyPlanCalculateCharge() {
        FlatLevyPlan plan = new FlatLevyPlan(100, 5);
        assertEquals(100, plan.calculateChargeNgwee());
    }

    @Test
    public void testBasePlusSurchargeCalculateCharge() {
        BasePlusSurchargeLevyPlan plan = new BasePlusSurchargeLevyPlan(100, 50, 5);
        assertEquals(150, plan.calculateChargeNgwee());
    }

    @Test
    public void buildsSurchargePlanFromFactory() {
        LevyPlan plan = LevyPlanFactory.fromRow("BASE_PLUS_SURCHARGE", 100, 50, 5);
        assertEquals(150, plan.calculateChargeNgwee());
        assertEquals(5, plan.getGraceDays());
    }

    @Test 
    public void rejectsUnknownPlanType() {
        assertThrows(IllegalArgumentException.class, () -> {
            LevyPlanFactory.fromRow("PREMIUM", 100, 50, 5);
        });
    }
}