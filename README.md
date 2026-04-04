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

## Project Structure
Finance/
│── pom.xml
│
└── src/
├── main/
│ ├── java/
│ │ └── backend/
│ │ ├── controller/
│ │ ├── database/
│ │ │ └── enums/
│ │ ├── dto/
│ │ ├── repository/
│ │ ├── security/
│ │ │ └── component/
│ │ └── service/
│ │
│ └── resources/
│
└── test/
└── java/


