# Banking System Simulation

A Java-based banking application that simulates core financial operations using object-oriented programming and MySQL database integration. The system supports secure user authentication, account management, and transaction processing while maintaining data consistency through transactional controls.

## Key Features
- **Secure Authentication:** User registration and login with SHA-256 password hashing and password validation.  
- **Account Management:** Supports creation of Savings and Current accounts.  
- **Core Banking Operations:** Deposit, withdrawal, and fund transfer functionality.  
- **Transaction History:** Stores and retrieves transaction records for selected accounts.  
- **Database Persistence:** Uses MySQL and JDBC to manage users, accounts, and transactions.  
- **Transaction Integrity:** Implements commit/rollback mechanisms for secure financial operations.  
- **Exception Handling:** Custom exception handling for invalid transactions and insufficient balance scenarios.

## Tech Stack
- **Language:** Java  
- **Database:** MySQL  
- **Connectivity:** JDBC  
- **Concepts:** OOP, Exception Handling, SQL Transactions

## Program Flow
**Authentication:** Users register or log in securely using hashed credentials.  

**Account Setup:** Users can create and manage savings/current accounts.  

**Transaction Processing:** Selected accounts support deposits, withdrawals, and transfers.  

**History Tracking:** Transaction records are stored and retrieved from the database.  

**Consistency Control:** Commit/rollback ensures reliable multi-step transactions.
