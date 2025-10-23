# Stock Trading Simulation System

A comprehensive Spring Boot application that simulates a stock trading platform with real-time stock data, user authentication, portfolio management, and trading capabilities.

## 📋 Table of Contents

- [Features](#features)
- [Technology Stack](#technology-stack)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Database Schema](#database-schema)
- [Security](#security)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [License](#license)

## ✨ Features

### Core Features
- **User Authentication & Authorization**
  - JWT-based authentication
  - Secure user registration and login
  - Password encryption with BCrypt

- **Stock Market Integration**
  - Real-time stock quotes via Alpha Vantage API
  - Stock overview and company information
  - Historical stock data (daily, weekly, monthly, intraday)
  - Market news and sentiment analysis

- **Trading Capabilities**
  - Buy stocks with virtual balance
  - Sell stocks from portfolio
  - Automatic profit/loss calculation
  - Transaction history tracking

- **Portfolio Management**
  - Real-time portfolio valuation
  - Individual stock performance tracking
  - Total investment and current value calculation
  - Profit/loss analysis per stock

- **Performance Optimization**
  - Caffeine-based caching for API responses
  - Configurable cache expiration policies
  - Database query optimization

## 🛠 Technology Stack

### Backend
- **Framework**: Spring Boot 3.x
- **Security**: Spring Security with OAuth2 Resource Server
- **Database**: JPA/Hibernate with PostgreSQL/MySQL
- **Caching**: Caffeine Cache
- **API Client**: WebFlux WebClient
- **Authentication**: JWT (JSON Web Tokens)

### Build Tools
- **Maven**: Dependency management and build automation
- **Lombok**: Reduce boilerplate code

### External APIs
- **Alpha Vantage**: Stock market data and news

## 📦 Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17** or higher
- **Maven 3.8+**
- **PostgreSQL 14+** or **MySQL 8+**
- **Alpha Vantage API Key** (Get it free at [alphavantage.co](https://www.alphavantage.co/support/#api-key))

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/stock-trading-simulation.git
cd stock-trading-simulation
```

### 2. Configure Database

Create a database for the application:

```sql
CREATE DATABASE stock_trading_db;
```

### 3. Configure Application Properties

Create or update `src/main/resources/application.properties`:

```properties
# Server Configuration
server.port=8080

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/stock_trading_db
spring.datasource.username=your_db_username
spring.datasource.password=your_db_password
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

# Alpha Vantage API Configuration
alpha.vantage.api.key=YOUR_API_KEY_HERE
alpha.vantage.base.url=https://www.alphavantage.co/query

# JWT Configuration
security.jwt.secret=your-secret-key-minimum-256-bits-long-for-hs256-algorithm
jwt.expiration=86400000

# Logging
logging.level.com.pranjal=DEBUG
logging.level.org.springframework.security=DEBUG
```

### 4. Build the Application

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

## ⚙️ Configuration

### Cache Configuration

The application uses Caffeine cache with the following settings (configurable in `CacheConfig.java`):

| Cache Name | Expiration | Max Size |
|------------|------------|----------|
| stockQuotes | 1 hour | 200 |
| stockOverview | 1 day | 200 |
| stockHistory | 1 day | 200 |
| stockNews | 6 hours | 200 |
| portfolio | 1 hour | 200 |

### Security Configuration

- **CORS**: Configured for `http://localhost:8080`
- **Session Management**: Stateless (JWT-based)
- **Password Encoding**: BCrypt

## 📚 API Documentation

### Authentication Endpoints

#### Register User
```http
POST /api/v1/auth/register
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123",
  "name": "John Doe"
}
```

**Response:**
```json
{
  "userId": "uuid",
  "email": "user@example.com",
  "name": "John Doe",
  "virtualBalance": 1000.00,
  "createdAt": "2025-10-23T10:30:00"
}
```

#### Login
```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "email": "user@example.com",
  "password": "securePassword123"
}
```

**Response:**
```json
{
  "email": "user@example.com",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "name": "John Doe",
  "balance": 1000.00
}
```

### Stock Endpoints

#### Get Stock Quote
```http
GET /api/v1/stocks/quote?symbol=AAPL
Authorization: Bearer {token}
```

**Response:**
```json
{
  "symbol": "AAPL",
  "price": 178.50
}
```

#### Get Stock Overview
```http
GET /api/v1/stocks/overview?symbol=AAPL
Authorization: Bearer {token}
```

**Response:**
```json
{
  "symbol": "AAPL",
  "name": "Apple Inc",
  "description": "Apple Inc. designs, manufactures, and markets...",
  "exchange": "NASDAQ",
  "country": "USA",
  "officialSite": "https://www.apple.com",
  "sector": "Technology"
}
```

#### Get Stock History
```http
GET /api/v1/stocks/history?symbol=AAPL&days=30&function=TIME_SERIES_DAILY
Authorization: Bearer {token}
```

**Response:**
```json
[
  {
    "date": "2025-10-22",
    "open": 177.50,
    "high": 179.20,
    "low": 176.80,
    "close": 178.50,
    "volume": 45678900
  }
]
```

#### Get Stock News
```http
GET /api/v1/stocks/news?size=10
Authorization: Bearer {token}
```

### Trading Endpoints

#### Buy Stock
```http
POST /api/v1/trade/buy
Authorization: Bearer {token}
Content-Type: application/json

{
  "symbol": "AAPL",
  "quantity": 10
}
```

**Response:**
```json
{
  "transactionId": "uuid",
  "type": "BUY",
  "stockSymbol": "AAPL",
  "pricePerUnit": 178.50,
  "quantity": 10,
  "totalAmount": 1785.00,
  "createdAt": "2025-10-23T10:30:00"
}
```

#### Sell Stock
```http
POST /api/v1/trade/sell
Authorization: Bearer {token}
Content-Type: application/json

{
  "symbol": "AAPL",
  "quantity": 5
}
```

**Response:**
```json
{
  "transactionId": "uuid",
  "type": "SELL",
  "stockSymbol": "AAPL",
  "pricePerUnit": 180.00,
  "quantity": 5,
  "totalAmount": 900.00,
  "createdAt": "2025-10-23T11:00:00",
  "profit": true,
  "profitOrLoss": 75.00
}
```

### Portfolio Endpoints

#### Get User Portfolio
```http
GET /api/v1/users/portfolio
Authorization: Bearer {token}
```

**Response:**
```json
{
  "totalStocks": 2,
  "totalInvestedValue": 2500.00,
  "totalCurrentValue": 2650.00,
  "totalProfitLoss": 150.00,
  "stockSummary": [
    {
      "symbol": "AAPL",
      "quantity": 10,
      "investedValue": 1785.00,
      "currentValue": 1800.00,
      "profitLoss": 15.00
    },
    {
      "symbol": "GOOGL",
      "quantity": 5,
      "investedValue": 715.00,
      "currentValue": 850.00,
      "profitLoss": 135.00
    }
  ]
}
```

## 🗄️ Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    user_id VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    virtual_balance DECIMAL(15,2) DEFAULT 1000.00,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### Holdings Table
```sql
CREATE TABLE holdings (
    id BIGSERIAL PRIMARY KEY,
    holding_id VARCHAR(255) UNIQUE NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    quantity INTEGER NOT NULL,
    average_price DECIMAL(15,2) NOT NULL,
    user_id BIGINT REFERENCES users(id),
    UNIQUE(user_id, stock_symbol)
);
```

### Transactions Table
```sql
CREATE TABLE transactions (
    id BIGSERIAL PRIMARY KEY,
    transaction_id VARCHAR(255) UNIQUE NOT NULL,
    stock_symbol VARCHAR(10) NOT NULL,
    type VARCHAR(10) NOT NULL,
    price_per_unit DECIMAL(15,2) NOT NULL,
    quantity INTEGER NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_id BIGINT REFERENCES users(id)
);
```

## 🔒 Security

### Authentication Flow

1. User registers with email and password
2. Password is encrypted using BCrypt
3. User logs in with credentials
4. JWT token is generated with 24-hour expiration
5. Token must be included in Authorization header for protected endpoints
6. Token format: `Authorization: Bearer {token}`

### Authorized Endpoints

All endpoints except the following require authentication:
- `/api/v1/auth/register`
- `/api/v1/auth/login`
- Swagger UI documentation endpoints
- Static resources (HTML, CSS, JS)

## 📁 Project Structure

```
src/
├── main/
│   ├── java/com/pranjal/
│   │   ├── client/              # External API clients
│   │   │   └── StockClient.java
│   │   ├── config/              # Configuration classes
│   │   │   ├── CacheConfig.java
│   │   │   ├── SecurityConfig.java
│   │   │   └── WebClientConfig.java
│   │   ├── controller/          # REST controllers
│   │   │   ├── AuthController.java
│   │   │   ├── PortfolioController.java
│   │   │   ├── StockController.java
│   │   │   ├── TradingController.java
│   │   │   └── GlobalExceptionHandler.java
│   │   ├── dtos/                # Data Transfer Objects
│   │   │   ├── AlphaVantageDTOs/
│   │   │   ├── AuthenticationDTOs/
│   │   │   ├── StocksDTOs/
│   │   │   ├── TradingDTOs/
│   │   │   └── PortfolioResponse.java
│   │   ├── exception/           # Custom exceptions
│   │   ├── model/               # JPA entities
│   │   │   ├── User.java
│   │   │   ├── Holding.java
│   │   │   └── Transaction.java
│   │   ├── repository/          # JPA repositories
│   │   ├── service/             # Business logic
│   │   │   ├── StockService.java
│   │   │   ├── TradingService.java
│   │   │   ├── PortfolioService.java
│   │   │   └── UserService.java
│   │   └── util/                # Utility classes
│   │       └── JwtUtility.java
│   └── resources/
│       ├── application.properties
│       └── static/              # Frontend resources
└── test/                        # Test classes
```

## 🚦 Error Handling

The application provides standardized error responses:

```json
{
  "message": "Error description",
  "timestamp": "2025-10-23T10:30:00",
  "status": "HTTP_STATUS_CODE"
}
```

### Common Error Codes

| Status Code | Error | Description |
|-------------|-------|-------------|
| 400 | BAD_REQUEST | Invalid request format or validation failure |
| 401 | UNAUTHORIZED | Invalid or missing authentication token |
| 404 | NOT_FOUND | Resource not found (user, stock, transaction) |
| 409 | CONFLICT | Business rule violation (insufficient balance/holdings) |
| 500 | INTERNAL_SERVER_ERROR | Server error or external API failure |

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request
   
## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👨‍💻 Author

**Pranjal**

## 🙏 Acknowledgments

- [Alpha Vantage](https://www.alphavantage.co/) for providing stock market data API

---

**Note**: This is a simulation platform for educational purposes. Virtual currency has no real-world value. Always consult financial advisors for real investment decisions.
