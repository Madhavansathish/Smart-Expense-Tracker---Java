# 💰 Smart Expense Tracker (JavaFX + MySQL)

A simple and smart expense tracking application built using **JavaFX** for the UI and **MySQL** for database management.  
This app allows users to register, log in, add expenses, and visualize them through charts.

---

## 🚀 Features
- User registration and login system  
- Add and view daily expenses  
- Pie chart showing category-wise spending  
- Bar chart for monthly expense summary  
- Data stored in MySQL database  
- Simple and clean JavaFX interface  

---

## 🛠️ Requirements
- Java 21 or later  
- JavaFX SDK 21  
- MySQL Server  
- MySQL Connector/J (JDBC Driver)

---

## ⚙️ Setup Instructions

### 1. Database Setup
Open MySQL and create a database using the following commands:
```sql
CREATE DATABASE expense_tracker;
USE expense_tracker;

CREATE TABLE users (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE,
  email VARCHAR(100),
  password VARCHAR(100),
  full_name VARCHAR(100)
);

CREATE TABLE categories (
  category_id INT AUTO_INCREMENT PRIMARY KEY,
  category_name VARCHAR(50)
);

CREATE TABLE expenses (
  expense_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  category_id INT,
  amount DOUBLE,
  description VARCHAR(255),
  expense_date DATE,
  FOREIGN KEY (user_id) REFERENCES users(user_id),
  FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

INSERT INTO categories (category_name)
VALUES ('Food'), ('Travel'), ('Shopping'), ('Bills'), ('Entertainment');



2. Configure Database Connection

Open DatabaseConnection.java and update your MySQL credentials:

private static final String URL = "jdbc:mysql://localhost:3306/expense_tracker";
private static final String USER = "root";
private static final String PASSWORD = "your_mysql_password";



▶️ Run Instructions
Compile
javac --module-path "D:\javafx-sdk-21.0.9\lib" --add-modules javafx.controls -cp "lib/mysql-connector-j-9.5.0.jar;src" -d out src\*.java

Run
java --module-path "D:\javafx-sdk-21.0.9\lib" --add-modules javafx.controls -cp


👤 Author
Madhavan Sathish

