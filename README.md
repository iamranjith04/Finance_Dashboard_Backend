# Finance Dashboard Backend

A **Spring Boot–based backend application** for managing a finance dashboard. This system enables administrators to manage transaction records securely while supporting role-based access for different users.

---

## Features:

- **JWT Authentication**
  - Secure login/signup using JSON Web Tokens
  - Token validity: **1 day**

- **Role-Based Access Control**
  - **Admin**
    - Add, delete, update, and view transactions
    - Create users (Viewer / Analyst)
  - **Viewer**
    - View dashboard records only
  - **Analyst**
    - View and analyze financial records

- **Admin Verification**
  - Admin access is granted only if the signup secret matches:
    ```
    src/main/resources/AdminSecret.json
    ```

- **Transaction Management**
  - Manage transactions for specific accounts

- **Pessimistic Locking**
  - Prevents concurrent transaction conflicts
  - Ensures data consistency at the database level

---

## Tech Stack

- **Backend**: Spring Boot
- **Security**: Spring Security + JWT
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA / Hibernate

---

## ⚙️ How to Run the Project

### 🔧 Prerequisites

Make sure you have installed:

- Java 17+
- Maven
- PostgreSQL

---

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/Finance_Dashboard_Backend.git
cd Finance_Dashboard_Backend
```

## 2. CREATE DATABASE financedb;

## 3. Configure Environment Variables

## 4. Run the Application




