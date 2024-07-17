# Advanced Financial Services Management System

## Description
The Advanced Financial Services Management System is a comprehensive platform developed using Java and Spring technologies. It facilitates account management, transaction processing, investment portfolio management, customer service, and financial advisory.

## Business Case
Financial services play a crucial role in the economy, and an efficient management system can significantly enhance operational efficiency and accuracy. This project aims to provide developers with insights into building robust financial service applications, focusing on security, data integrity, and user interaction.

## Suggested Architecture & Database Description
The system comprises two microservices, each with its own database:

1. **Account Management Service**: Manages user registration, login, account operations, and transactions (deposit, withdrawal, transfer).
2. **Investment and Advisory Service**: Handles investment management, customer service requests, and advisory sessions.

### Communication between Services
- **Scenario**: A user wants to invest in a new financial product.
- **Steps**:
    1. User logs in and navigates to investments.
    2. Investment service checks account balance by querying Account Management.
    3. Investment service processes investment if funds are sufficient.
    4. User receives notification of successful investment.

### Account Management Service
- **Database Schema:**
    - **Users**: `id`, `name`, `email`, `password`
    - **Accounts**: `id`, `user_id`, `account_type`, `balance`
    - **Transactions**: `id`, `account_id`, `type`, `amount`, `date`
- **API Requirements:**
    - **Authentication and Session Management:**
        - `POST /auth/register`: Register a new user.
        - `POST /auth/login`: Authenticate user and create a session.
        - `POST /auth/logout`: Terminate the session.
    - **Account Operations:**
        - `POST /accounts`: Create a new account.
        - `GET /accounts`: List all user's accounts.
        - `PUT /accounts/{id}`: Update an existing account.
        - `DELETE /accounts/{id}`: Delete an account.
    - **Transaction Operations:**
        - `POST /transactions/deposit`: Deposit funds into an account.
        - `POST /transactions/withdraw`: Withdraw funds from an account.
        - `POST /transactions/transfer`: Transfer funds between accounts.
        - `GET /transactions`: View transaction history.

### Investment and Advisory Service
- **Database Schema:**
    - **Investments**: `id`, `user_id`, `investment_type`, `amount`, `date`
    - **CustomerServiceRequests**: `id`, `user_id`, `request_type`, `description`, `status`
    - **FinancialAdvisors**: `id`, `name`, `specialization`
    - **AdvisorySessions**: `id`, `user_id`, `advisor_id`, `date`, `time`, `status`
- **API Requirements:**
    - **Investment Operations:**
        - `POST /investments`: Create a new investment.
        - `GET /investments`: List all user's investments.
        - `PUT /investments/{id}`: Update an existing investment.
        - `DELETE /investments/{id}`: Delete an investment.
    - **Customer Service Operations:**
        - `POST /service-requests`: Create a new service request.
        - `GET /service-requests`: List all user's service requests.
        - `PUT /service-requests/{id}`: Update an existing service request.
    - **Advisory Session Operations:**
        - `POST /advisory-sessions`: Schedule a new advisory session.
        - `GET /advisory-sessions`: List all user's advisory sessions.
        - `PUT /advisory-sessions/{id}`: Reschedule an existing advisory session.
        - `DELETE /advisory-sessions/{id}`: Cancel an advisory session.

## Installation
1. **Clone the repository:**
   ```sh
   git clone <repository_url>
   cd <repository_directory>
   ```
   
2. **Database Setup:

- Ensure PostgreSQL is installed and running.
- Configure database settings in application.properties.

3. **Build and Run:**

```sh
./mvnw clean install
cd account-management-service
./mvnw spring-boot:run
cd ../investment-advisory-service
./mvnw spring-boot:run
```
