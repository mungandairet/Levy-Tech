# Levy-Tech
### Market Levy Subscription and Stall Tracker

A Java backend built with Javalin that helps market committees in Ndola manage trader registrations, stall allocations, levy subscriptions, payments and arrears, replacing handwritten records with a digital system.

**Course:** Object-Oriented Programming  
**Institution:** Zambia University College of Technology

## Group Members and Responsibilities

1. **Tatenda Mungandaire**
   - Student ID: 2420686
   - Responsibility: Billing (Levy plans and BillingService)

2. **Elijah Nde**
   - Student ID: 2420934
   - Responsibility: Domain and API (Trader, Stall and Market classes)

3. **Emmanuel Mwaba**
   - Student ID: 2420928
   - Responsibility: Database (Schema, seed data and DAOs)

4. **Chewe Sichela**
   - Student ID: 2410110
   - Responsibility: Payments and Exceptions

5. **Meleki Stephen**
   - Student ID: 2420966
   - Responsibility: Frontend (Web pages and user interface)

## Main Classes

- **Trader** – Manages trader details, balances, arrears and stall assignments.
- **Stall** – Manages market stalls and prevents double allocation.
- **Payment** – Records individual payment transactions.
- **LevyPlan** – Abstract parent class defining levy calculation rules.
- **FlatLevyPlan** – Calculates a fixed daily levy.
- **BasePlusSurchargePlan** – Calculates the base levy plus an additional surcharge.
- **LedgerEntry** – Records financial transactions and balance changes.

## Technologies Used

- Java
- Javalin
- SQLite
- Maven
- JUnit 5

