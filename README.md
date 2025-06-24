# HBank API

A banking API built with Spring Boot and Kotlin.

## Features

- **User Management**
  - User registration and authentication
  - Email verification
  - Password reset functionality

- **Account Management**
  - Personal account information
  - Account status tracking

- **Transaction Management**
  - View transaction history
  - Paginated transaction listings

- **Transfer Capabilities**
  - Money transfers between accounts

- **Customer Management**
  - Customer information handling

- **Request Processing**
  - Handle various banking requests

## Technologies Used

- **Backend**
  - Kotlin 
  - Spring Boot
  - Spring Security
  - Spring Data JPA
  - PostgreSQL
  - Thymeleaf

- **Build & Deployment**
  - Gradle
  - Docker Compose

- **Testing**
  - JUnit 5
  - TestContainers
  - Spring Boot Test
  - Mockito & MockK

## Getting Started

### Prerequisites

- JDK 21
- Docker
- PostgreSQL (or use the provided Docker configuration)

### Setup

1. Clone the repository:
   ```
   git clone https://github.com/yourusername/hbank-api.git
   cd hbank-api
   ```

2. Build the project:
   ```
   ./gradlew build
   ```

3. Run with Docker Compose:
   ```
   docker-compose up
   ```

   Or run locally:
   ```
   ./gradlew bootRun
   ```

### Configuration

The application uses Spring Boot's configuration system. Key configuration files:

- `application.properties` or `application.yml` - Main configuration
- `compose.yaml` - Docker Compose configuration

## API Endpoints

### User Management

- `POST /api/v1/users/register` - Register a new user
- `POST /api/v1/users/verify/email/send` - Send verification email
- `POST /api/v1/users/verify/email` - Verify email
- `POST /api/v1/users/password/forgot` - Forgot password
- `POST /api/v1/users/password/reset` - Reset password

### Account Management

- `GET /api/v1/accounts/personal` - Get personal account information

### Transaction Management

- `POST /api/v1/transactions` - Get paginated transaction history

## Security

The application implements Spring Security with OAuth2 for authentication and authorization. JWT tokens are used for maintaining user sessions.

## Testing

Run tests with:
```
./gradlew test
```

## Building for Production

### Standard JAR

```
./gradlew build
```

## Project Structure

```
src
├── main
│   ├── kotlin
│   │   └── org
│   │       └── example
│   │           └── hbank
│   │               └── api
│   │                   ├── config        # Configuration classes
│   │                   ├── controller    # REST controllers
│   │                   ├── mapper        # Object mappers
│   │                   ├── model         # Data models
│   │                   ├── repository    # Data repositories
│   │                   ├── request       # Request DTOs
│   │                   ├── response      # Response DTOs
│   │                   ├── service       # Business logic
│   │                   └── util          # Utility classes
│   └── resources
│       ├── certificate  # Security certificates
│       ├── database     # Database scripts
│       ├── static       # Static resources
│       └── templates    # Thymeleaf templates
└── test
    ├── kotlin          # Test classes
    └── resources       # Test resources
```

## License

```
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
```
