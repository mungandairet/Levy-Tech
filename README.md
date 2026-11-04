# Levy-Tech
### Market Levy Subscription and Stall Tracker

A Java backend built with Javalin that helps market committees in Ndola manage trader registrations, stall allocations, levy subscriptions, payments and arrears, replacing handwritten records with a digital system.

**Course:** Object-Oriented Programming  
**Institution:** Zambia University College of Technology

## 1. The Problem

Market committees in Ndola often rely on handwritten records to manage traders, allocate stalls and track levy payments. This can lead to lost records, calculation errors and difficulties identifying traders who have not paid. Levy-Tech provides a digital system to make these activities easier, more accurate and organised.

## 2. How to Run

**Requirements:**
- Install Java 17 or later.
- Install Apache Maven.
- Clone the Levy-Tech repository from GitHub.
- Open the project in an IDE such as IntelliJ IDEA.

**Steps:**
1. Open the project folder in your IDE.
2. Allow Maven to download the required dependencies.
3. Open `src/main/java/com/levytech/Main.java`.
4. Run the `Main` class to start the Javalin application.
5. Open your browser and visit `http://localhost:7070` if the application is configured to use port 7070.

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

## 3. Main Classes

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