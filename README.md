# Ekart-Application-

## Requirements

- Java 17
- MySQL, with the application database and tables configured

## Run locally

Set the database credentials and a Base64-encoded JWT secret in your shell. Do not commit real credentials:

```bash
export DB_URL='jdbc:mysql://localhost:3306/billa2024?allowPublicKeyRetrieval=true&useSSL=false'
export DB_USERNAME='root'
export DB_PASSWORD='your-local-mysql-password'
export JWT_SECRET="$(openssl rand -base64 32)"
bash mvnw spring-boot:run
```

On Windows PowerShell, set the same variables with `$env:DB_URL`, `$env:DB_USERNAME`, `$env:DB_PASSWORD`, and `$env:JWT_SECRET`, then run `.\mvnw.cmd spring-boot:run`.

The API listens on port 8080 by default. `GET /auth/v1/mes` is a public status endpoint.
