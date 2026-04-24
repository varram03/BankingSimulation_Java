# Banking System Simulation

A Java-based banking application that simulates core banking operations using Object-Oriented Programming, Swing GUI, and MySQL database integration. The system supports secure user authentication, account management, transaction processing, and transaction history while maintaining data consistency through database transactions.

## Features

**Secure Authentication**: User registration and login with SHA-256 password hashing and password validation.

**Account Management**: Create, manage, and switch between Savings and Current accounts.

**Banking Operations**: Perform deposits, withdrawals, and fund transfers with balance validation.

**Transaction History**: View stored records of deposits, withdrawals, and transfers.

**Database Persistence**: Manage users, accounts, and transactions using MySQL and JDBC.

**Transaction Integrity**: Uses commit/rollback to ensure reliable financial operations.

**Exception Handling**: Handles invalid transactions and insufficient balance scenarios.

## Tech Stack
- **Language:** Java  
- **GUI:** Java Swing  
- **Database:** MySQL  
- **Connectivity:** JDBC  
- **Concepts:** OOP, Exception Handling, SQL Transactions  

## Program Flow
1. User registers or logs in securely  
2. Creates or selects a Savings/Current account  
3. Performs deposits, withdrawals, or transfers  
4. Views transaction history  
5. Database transactions ensure consistency and reliability  

## Database Tables
- Users  
- Accounts  
- Transactions  

## Highlights
- Object-oriented modular design  
- Secure password handling with hashing  
- Transaction-safe fund transfers  
- GUI and console support included  
