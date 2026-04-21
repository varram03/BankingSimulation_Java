USE banking;

DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS accounts;
DROP TABLE IF EXISTS users;

-- USERS TABLE
CREATE TABLE users (
    userId VARCHAR(50) PRIMARY KEY,
    password VARCHAR(100)  
);

-- ACCOUNTS TABLE
CREATE TABLE accounts (
    accNo VARCHAR(20) PRIMARY KEY,
    userId VARCHAR(50),
    accType VARCHAR(20),
    balance DECIMAL(15,2),
    FOREIGN KEY (userId) REFERENCES users(userId)
);

-- TRANSACTIONS TABLE
CREATE TABLE transactions (
    tid INT AUTO_INCREMENT PRIMARY KEY,
    accNo VARCHAR(20),
    type VARCHAR(20), 
    amount DOUBLE,
    description VARCHAR(255),
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (accNo) REFERENCES accounts(accNo)
);