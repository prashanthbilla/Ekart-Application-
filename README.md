# Ekart Application

Ekart is a REST API for an e-commerce application. The backend is built with Spring Boot and Java, and provides user authentication, products, carts, orders, payments, and wallet operations.

> This repository contains the backend. Run the Angular frontend separately.

## Technology

- Java 17
- Spring Boot 3.1.12
- Spring Web and Spring Security
- Spring Data JDBC
- MySQL
- JWT authentication

## Prerequisites

- JDK 17
- MySQL Server
- The project's MySQL database and tables

The application’s default database URL points to `billa2024` on `localhost:3306`. This repository does not include database schema or migration scripts, so the required database and tables must already exist.

## Local configuration

The application uses the `local` Spring profile. Keep local credentials and secrets in:

```text
src/main/resources/application-local.properties
```

That file is excluded from Git. Set its datasource password to the password for your local MySQL account, and set a Base64-encoded JWT secret of at least 32 bytes:

```properties
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/billa2024?allowPublicKeyRetrieval=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=YOUR_LOCAL_MYSQL_PASSWORD
app.jwt.secret=YOUR_BASE64_ENCODED_SECRET
```

Generate a JWT secret once and keep it stable between runs. In PowerShell:

```powershell
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$key = New-Object byte[] 32
$rng.GetBytes($key)
$rng.Dispose()
[Convert]::ToBase64String($key)
```

Copy the printed value to `app.jwt.secret`. Do not commit or share this local configuration file.

## Run from IntelliJ IDEA

1. Open the project and allow Maven to import dependencies.
2. Confirm MySQL is running, the `billa2024` database and required tables exist, and the local MySQL credentials are correct.
3. Run `Ekart2Application`.

The default server port is `8080`. If that port is already in use, stop the process using it or set a different `server.port` in the local properties file.

## Run from Windows PowerShell

From the project root:

```powershell
.\mvnw.cmd spring-boot:run
```

## Build and tests

```powershell
.\mvnw.cmd clean install
```

The application-context test starts Spring and needs a working database connection. If the build fails during tests, check the MySQL service, database name, and credentials.

## API overview

Base URL: `http://localhost:8080`

| Area | Method | Endpoint | Purpose |
|---|---|---|---|
| Status | GET | `/auth/v1/mes` | Public status response |
| Authentication | POST | `/auth/v1/authenticate` | Verify credentials and return a JWT |
| Users | POST | `/user/v1/post` | Register a user |
| Users | GET, PUT, DELETE | `/user/v1` | Retrieve, update, or delete user data |
| Users | GET | `/user/v1/all`, `/user/v1/getRole` | List users or retrieve a role |
| Products | GET, POST, PUT, DELETE | `/product/v1` | Retrieve or manage products |
| Products | GET | `/product/v1/all` | List products, optionally filtered by category |
| Cart | GET | `/kart/v1` | Retrieve cart details |
| Cart | PUT | `/kart/v1/addproducts` | Add products to a cart |
| Cart | DELETE | `/kart/v1/delete` | Remove a product from a cart |
| Orders | GET | `/order/v1/getAll`, `/order/v1/getOrders`, `/order/v1/get` | Retrieve order information |
| Payments | PUT | `/payment/v1/products`, `/payment/v1/buysomeproducts` | Purchase cart items |
| Payments | GET | `/payment/v1/getAll`, `/payment/v1/get` | Retrieve payment information |
| Wallet | GET | `/wallet/v1` | Check wallet balance |
| Wallet | PUT | `/wallet/v1/addmoney` | Add funds to a wallet |

Many API routes require authentication. Authenticate first, then send the returned JWT with protected requests:

```http
Authorization: Bearer YOUR_JWT_TOKEN
```

Authentication request body:

```json
{
  "username": "YOUR_USERNAME",
  "password": "YOUR_PASSWORD"
}
```

## Angular frontend

The Angular 17 application is in the `frontend/` directory. It uses Angular Material and calls the backend at `http://localhost:8080`. The backend CORS configuration allows the default Angular development origin, `http://localhost:4200`.

### Prerequisites

- Node.js 20 LTS (20.9 or later)
- npm

### Install and run

From the repository root:

```powershell
cd frontend
npm ci
npm start
```

Open `http://localhost:4200`. Keep the Spring Boot backend and MySQL running in separate terminals. The frontend's API URL is currently configured in its authentication service.

To build the frontend:

```powershell
cd frontend
npm run build
```

`node_modules/`, Angular's `.angular/` cache, and generated `dist/` files are excluded from Git.
