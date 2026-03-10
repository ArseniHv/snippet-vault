# Dev Snippet Vault

A production-grade REST API microservice for saving, organizing, and searching developer code snippets. Built with Spring Boot 3.5, MongoDB 7, and JWT authentication.

## Features

- Full CRUD for code snippets with title, content, language, description, and tags
- Organize snippets into named collections
- Full-text search across title, content, and description
- Filter snippets by programming language or tag
- Version history: every content edit preserves the previous version inside the document
- Fork any public snippet
- Usage analytics via MongoDB aggregation pipeline
- JWT-based authentication — private snippets require a valid token
- Paginated and sortable responses on all list endpoints
- Spring Boot Actuator health endpoint

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Database | MongoDB 7 |
| Auth | JWT (jjwt 0.12.5) |
| Build | Maven 3.9 |
| Testing | JUnit 5, Mockito |
| DevOps | Docker, Docker Compose, GitHub Actions |

## Why MongoDB?

This project is intentionally designed to justify MongoDB as the database of choice. Here is why a relational database would be a poor fit:

**1. Variable document structure**

A TypeScript snippet and a Bash snippet have nothing in common structurally. In SQL you would either create one wide table with many nullable columns, or a separate table per language — both are ugly. MongoDB stores each snippet as a self-describing document with no wasted columns.

**2. Embedded author object**
```json
"author": {
  "userId": "abc123",
  "username": "arsenihv"
}
```

In SQL this is a JOIN between `snippets` and `users` on every read. In MongoDB the author data is embedded directly in the snippet document — one read, no JOIN, no foreign key constraint to maintain.

**3. Array-typed tags field**
```json
"tags": ["typescript", "utils", "performance"]
```

In SQL this requires a `snippet_tags` junction table, a JOIN, and a GROUP BY to reassemble. MongoDB indexes each array element individually and queries like `{ tags: "typescript" }` are first-class citizens.

**4. Embedded version history**
```json
"versionHistory": [
  { "versionNumber": 1, "content": "...", "savedAt": "..." }
]
```

In SQL this is a separate `snippet_versions` table with a foreign key and an ORDER BY. In MongoDB the entire history lives inside the document — no extra query, no extra table, and the history is always returned atomically with the snippet.

**5. Full-text search as a first-class feature**

MongoDB's text index spans multiple fields with configurable weights — title matches rank higher than content matches, which rank higher than description matches. This is configured directly on the collection with no external search service needed.

**6. Aggregation pipeline for analytics**

All analytics — top languages, top tags, most viewed snippets — are computed entirely inside MongoDB using its aggregation pipeline. No data is pulled into Java for processing. This is the correct pattern: push computation to the database, return only the result.

## Project Structure
```
snippet-vault/
├── src/main/java/com/snippetvault/
│   ├── auth/              ← JWT security, registration, login
│   ├── snippet/           ← core domain: CRUD, search, fork, version history
│   ├── collection/        ← collections feature
│   ├── analytics/         ← aggregation pipeline stats
│   └── config/            ← Spring Security, MongoDB indexes, exception handling
├── .github/workflows/ci.yml
├── docker-compose.yml
├── Dockerfile
└── README.md
```

## Getting Started

### Prerequisites

- Java 21
- Maven 3.9
- Docker and Docker Compose

### Run locally with Docker Compose
```bash
git clone https://github.com/ArseniHv/snippet-vault.git
cd snippet-vault
mvn clean package -DskipTests
docker compose up --build
```

> The app image uses a pre-built jar. Always run `mvn clean package -DskipTests` before `docker compose up --build`.

The API will be available at `http://localhost:8080`.

### Run locally without Docker
```bash
docker compose up mongodb -d
mvn spring-boot:run
```

## Running Tests
```bash
mvn clean test
```

## API Reference

### Auth

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | None | Register a new user |
| POST | `/api/auth/login` | None | Login and receive JWT |

### Snippets

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/snippets` | Required | Create a snippet |
| GET | `/api/snippets/{id}` | Optional | Get snippet by ID |
| GET | `/api/snippets/me` | Required | Get your snippets |
| PUT | `/api/snippets/{id}` | Required | Update a snippet |
| PATCH | `/api/snippets/{id}/content` | Required | Update content, saves version history |
| DELETE | `/api/snippets/{id}` | Required | Delete a snippet |
| POST | `/api/snippets/{id}/fork` | Required | Fork a public snippet |
| GET | `/api/snippets/search` | None | Full-text search (`?q=keyword`) |
| GET | `/api/snippets/search/language/{lang}` | None | Filter by language |
| GET | `/api/snippets/search/tag/{tag}` | None | Filter by tag |

### Collections

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/collections` | Required | Create a collection |
| GET | `/api/collections` | Required | List your collections |
| GET | `/api/collections/{id}` | Required | Get collection by ID |
| PUT | `/api/collections/{id}` | Required | Update a collection |
| DELETE | `/api/collections/{id}` | Required | Delete a collection |
| GET | `/api/collections/{id}/snippets` | Optional | List snippets in collection |

### Analytics

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| GET | `/api/analytics/summary` | None | Top languages, tags, most viewed snippets |

### Actuator

| Method | Endpoint | Description |
|---|---|---|
| GET | `/actuator/health` | Application and MongoDB health |

## Pagination

All list endpoints support pagination and sorting via query parameters:
```
GET /api/snippets/me?page=0&size=20&sort=createdAt,desc
GET /api/snippets/search/language/typescript?page=0&size=10&sort=viewCount,desc
```