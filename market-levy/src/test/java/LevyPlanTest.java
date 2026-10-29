package com.levytech.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LevyPlanTest{
    @Test
    public void testFlatLevyPlanCalculateCharge(){ 
    //Arrange
     
    FlatLevyPlan plan flatLevyPlan = new FlatLevyPlan(100, 5);

    //Act
    assertEquals(100,plan.calculateCharge());
}
}
@Test
public void testTieredLevyPlanCalculateCharge(){
    //Arrange
    TieredLevyPlan plan = new TieredLevyPlan(100, 50, 5);

    //Act
    assertEquals(150, plan.calculateCharge());
}