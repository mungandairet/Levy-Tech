# Levy-Tech

** Market Levy Subscription and Stall Tracker **

A  Java backend built with Javalin that helps market communities in Ndola manage vendor stall allocations and levy subscriptions,
replacing handwritten registers

** Course: ** Object-Oriented Programming
**Institution : "" Zambia University College of Technology

## Group Members
Tatenda Mungandaire 
Elijah Nonde 
Emmanuel Mwaba
Chewe Sichela
Meleki  Stephen

### 3. List of Classes
  **Vendor** - Manages trader details, their prepaid balance, payment history, and arrears.
  **Stall** - Manages physical trading spaces and ensures a stall cannot be double-booked.
  **Payment** - Records individual transactions (credits and deductions).
   **SubscriptionPlan** - The abstract parent class for different market levy rules.
   **CbdPremiumPlan** - A subclass of SubscriptionPlan that calculates the higher CBD fees.
  **StandardMarketPlan** - A subclass of SubscriptionPlan that calculates standard market fees and discounts.

