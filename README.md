# Advanced Financial Services Management System

Welcome to the Advanced Financial Services Management System, a robust backend API-based solution designed to manage financial operations efficiently and securely.

## Overview

This project implements essential financial services functionalities using Java and Spring technologies. It focuses on backend APIs for account management, transaction processing, investment management, customer service, and financial advisory.

## Key Features

- **Account Management**: Create, manage, and perform transactions (deposit, withdrawal, transfer) across different account types.
- **Investment Management**: Manage investments, view portfolio, and initiate new investment transactions.
- **Customer Service**: Submit service requests and track their status.
- **Financial Advisory**: Schedule advisory sessions with specialized financial advisors.

## Security

Security is paramount in our system:
- Access to sensitive endpoints is restricted using Spring Security.
- Unauthorized access attempts are blocked from endpoints such as `/swagger-ui/**`, `/auth/**`, and `/check-balance`.

## API Documentation

Explore our API using Swagger:
- Swagger UI provides detailed documentation on how to interact with our APIs securely.
- View API endpoints, request parameters, response models, and error messages.

## Getting Started

As this is a backend-only project, you can:
- Clone the repository and configure it locally.
- Set up your environment with Java and Spring Boot.
- Use your favorite REST client (e.g., Postman) to interact with the APIs.

## Technologies Used

- Java
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL

## How to Use

1. **Authentication**: Use `/auth/login` to authenticate and obtain a session token.
2. **Account Management**: Manage accounts and perform transactions.
3. **Investment Management**: Create and manage investments.
4. **Customer Service**: Submit and track service requests.
5. **Advisory Services**: Book advisory sessions with financial advisors.

---

Enjoy exploring the capabilities of our Advanced Financial Services Management System!
