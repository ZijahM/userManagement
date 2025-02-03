# User Management System

A robust and secure user management system built with Spring Boot, featuring JWT authentication, device management, and profile management capabilities.

## Features

- **User Authentication**
  - Secure registration with email verification
  - JWT-based authentication
  - Password hashing using BCrypt
  - Multi-device support

- **Profile Management**
  - View and update user profile
  - Change parameters with current password verification
    

- **Device Management**
  - Track active devices
  - Logout from specific devices

## Technical Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security
- PostgreSQL
- JWT for authentication
- BCrypt for password hashing

## Overview
A robust user management system built with Spring Boot, providing authentication, profile management, and device tracking features.

## Current Version
- **Database**: H2 In-Memory Database
- Suitable for development and testing purposes

## Postman Collection
A Postman collection is included in the repository (`User-Management-System.postman_collection.json`) to help with API testing and exploration.

## Local Setup and Running

### Prerequisites
- Java 17 or higher
- Maven

### Steps to Run
1. Clone the repository
   ```bash
   git clone https://github.com/your-username/user-management-system.git
   cd user-management-system
   ```

2. Build the project
   ```bash
   mvn clean install
   ```

3. Run the application
   ```bash
   mvn spring-boot:run
   ```

4. Access the Application
   - Base URL: `http://localhost:8080`
   - H2 Console: `http://localhost:8080/h2-console`
     - JDBC URL: `jdbc:h2:mem:testdb`
     - Username: `sa`
     - Password: `password` (in a real case sceario this would not be in the readme file, rather in the config file)

## API Endpoints

### Authentication
- POST `/api/v1/auth/register` - Register new user
- POST `/api/v1/auth/authenticate` - Login
- GET `/api/v1/auth/verify` - Verify email

### Profile Management
- GET `/api/v1/profile` - Get user profile
- PUT `/api/v1/profile` - Update profile

### Device Management
- GET `/api/v1/devices` - List active devices
- POST `/api/v1/devices/{deviceId}/logout` - Logout specific device
- POST `/api/v1/devices/logout-all` - Logout all devices

## API Documentation
Refer to the Postman collection for detailed API endpoint information.

## Notes
- The H2 database is in-memory, meaning all data will be lost when the application is stopped
- Ideal for development, testing, and demonstration purposes

## Security Features

- Passwords are hashed using BCrypt
- JWT tokens for authentication
- Email verification required
- Protected endpoints using Spring Security
- Input validation using Jakarta Validation
- Device tracking with IP address and device info

## Testing

A Postman collection is included in the project root directory (`User-Management-System.postman_collection.json`) for testing all endpoints.

## Project Structure

```
src/main/java/com/example/usermanagementsystem/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── model/          # Entity classes
├── repository/     # JPA repositories
├── security/       # Security configurations
└── service/        # Business logic
```
**NOTE**
Since in the task document some of the features were crossed out, they are not mentioned in the readme, but there is some implementation in the code related to them.
