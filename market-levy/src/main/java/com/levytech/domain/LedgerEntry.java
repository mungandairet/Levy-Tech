package com.levytech.domain;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;

public class LedgerEntry {
    
   
    public enum Type { CREDIT, DEDUCTION, ARREARS }

    // This is the method signature you are calling in BillingService
    public static void insert(Connection c, int traderId, Type type, long amountNgwee, long balanceAfterNgwee, String cycle, LocalDate date, String desc) throws SQLException {
      
    }
}