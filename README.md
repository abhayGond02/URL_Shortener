# URL Shortener

A REST API that turns long URLs into short links and redirects visitors to the original address, built with Spring Boot and PostgreSQL.

## Tech Stack

- Java 17
- Spring Boot (Web MVC, Data JPA, Validation)
- PostgreSQL
- Maven

## Features

- Shorten any `http://` or `https://` URL into a 6-character code
- Redirect from the short link to the original URL with HTTP `302`
- Cryptographically random codes (`SecureRandom`) with a uniqueness check and a database unique constraint
- Request validation with clear JSON error responses (`400`, `404`)
- Health check endpoint

## Architecture

```
Client ──HTTP──▶ Controller ──▶ Service ──▶ Repository ──▶ PostgreSQL
```

| Layer | Class | Responsibility |
|---|---|---|
| Controller | `UrlShortenerController` | HTTP endpoints, request validation, building the short URL |
| Service | `UrlShortenerService` | Short code generation and lookup logic |
| Repository | `UrlMappingRepository` | Database access through Spring Data JPA |
| Entity | `UrlMapping` | `url_mapping` table: original URL, short code, creation time |
| DTOs | `ShortenUrlRequest`, `ShortenUrlResponse` | Request and response bodies |
| Errors | `GlobalExceptionHandler` | Consistent JSON error responses |

## API

### Shorten a URL

`POST /api/urls`

```json
{ "originalUrl": "https://www.google.com" }
```

Response `201 Created`:

```json
{
  "originalUrl": "https://www.google.com",
  "shortCode": "aZ3k9Q",
  "shortUrl": "http://localhost:8080/aZ3k9Q"
}
```

### Open a short link

`GET /{shortCode}` responds with `302 Found` and a `Location` header pointing to the original URL.

### Health check

`GET /api/health` returns `URL Shortner is running`.

### Errors

| Case | Status | Body |
|---|---|---|
| Missing or invalid URL | `400` | `{ "status": 400, "errors": { "originalUrl": "URL must start with http:// or https://" } }` |
| Unknown short code | `404` | `{ "status": 404, "error": "Short URL not found" }` |

## Running Locally

1. Install Java 17 and PostgreSQL.
2. Create the database:
   ```sql
   CREATE DATABASE url_shortener_db;
   ```
3. Provide your PostgreSQL password, either as an environment variable:
   ```bash
   export DB_PASSWORD=your_password
   ```
   or in `demo/demo/src/main/resources/secrets.properties` (ignored by Git):
   ```properties
   spring.datasource.password=your_password
   ```
4. Start the app:
   ```bash
   cd demo/demo
   ./mvnw spring-boot:run
   ```
5. The API runs at `http://localhost:8080`. The `url_mapping` table is created automatically on startup.
