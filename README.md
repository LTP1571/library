# Library REST API

A simple REST API for managing **authors** and their **books**, built with Spring Boot and PostgreSQL.
One author can have many books; each book belongs to exactly one author (one-to-many relationship).

## Tech stack

- Java 21
- Spring Boot 4.1.1 (Web MVC, Data JPA, Validation)
- PostgreSQL
- Maven (wrapper included)
- JUnit 5 + Mockito for unit tests

## Project structure

```
src/main/java
├── com/ltp/library
│   ├── Author.java, Book.java                  # JPA entities
│   ├── AuthorRepository.java, BookRepository.java
│   ├── AuthorService.java, BookService.java    # business logic
│   ├── AuthorController.java, BookController.java
│   ├── AuthorNotFoundException.java
│   ├── BookNotFoundException.java
│   ├── AuthorHasBooksException.java
│   └── GlobalExceptionHandler.java             # maps exceptions to HTTP responses
└── dto                                         # request/response objects
    ├── AuthorResponse, BookResponse
    ├── CreateAuthorRequest, UpdateAuthorRequest
    ├── CreateBookRequest, UpdateBookRequest
    └── ErrorResponse
```

Entities are never returned directly — every endpoint returns a DTO.

## Data model

| Table    | Columns                                         |
|----------|-------------------------------------------------|
| `author` | `id`, `name`                                    |
| `book`   | `id`, `title`, `author_id` → references `author(id)` |

`Book` is the owning side of the relationship (`@ManyToOne` + `@JoinColumn(name = "author_id")`);
`Author` maps the reverse side with `@OneToMany(mappedBy = "author")`.

## Getting started

### Prerequisites

- JDK 21
- PostgreSQL running on `localhost:5432`

### 1. Create the database

```sql
CREATE DATABASE library;
```

Tables are created automatically by Hibernate on first run (`spring.jpa.hibernate.ddl-auto=update`).

### 2. Set the database password

The password is read from an environment variable, not stored in the code.

Windows (PowerShell):
```powershell
$env:DB_PASSWORD="your_password"
```

macOS / Linux:
```bash
export DB_PASSWORD=your_password
```

If you run the app from IntelliJ, add `DB_PASSWORD` under **Run Configuration → Environment variables** instead.

The default database user is `postgres`. Change it in `src/main/resources/application.properties` if needed.

### 3. Run the application

```bash
./mvnw spring-boot:run
```

The API starts at `http://localhost:8080`.

## API endpoints

### Authors — `/api/authors`

| Method | Path                | Description         | Success | Errors   |
|--------|---------------------|---------------------|---------|----------|
| GET    | `/api/authors`      | List all authors    | 200     | —        |
| GET    | `/api/authors/{id}` | Get one author      | 200     | 404      |
| POST   | `/api/authors`      | Create an author    | 201     | 400      |
| PUT    | `/api/authors/{id}` | Update an author    | 200     | 400, 404 |
| DELETE | `/api/authors/{id}` | Delete an author    | 204     | 404, 409 |

An author who still has books **cannot be deleted** — the API returns `409 Conflict`. Delete the author's books first.

### Books — `/api/books`

| Method | Path              | Description       | Success | Errors   |
|--------|-------------------|-------------------|---------|----------|
| GET    | `/api/books`      | List all books    | 200     | —        |
| GET    | `/api/books/{id}` | Get one book      | 200     | 404      |
| POST   | `/api/books`      | Create a book     | 201     | 400, 404 |
| PUT    | `/api/books/{id}` | Update a book     | 200     | 400, 404 |
| DELETE | `/api/books/{id}` | Delete a book     | 204     | 404      |

Creating or updating a book returns `404` if the given `authorId` does not exist.

`POST` requests return a `Location` header pointing to the new resource, e.g. `Location: /api/books/3`.

## Request and response examples

### Create an author

```http
POST /api/authors
Content-Type: application/json

{
  "name": "Plato"
}
```

Response `201 Created`:
```json
{
  "id": 1,
  "name": "Plato"
}
```

### Create a book

```http
POST /api/books
Content-Type: application/json

{
  "title": "The Republic",
  "authorId": 1
}
```

Response `201 Created`:
```json
{
  "id": 1,
  "title": "The Republic",
  "author": {
    "id": 1,
    "name": "Plato"
  }
}
```

### Validation rules

| Request               | Field      | Rule                          |
|-----------------------|------------|-------------------------------|
| Create/Update author  | `name`     | required, not blank           |
| Create/Update book    | `title`    | required, not blank           |
| Create/Update book    | `authorId` | required                      |

## Error format

All errors share the same response body:

```json
{
  "message": "...",
  "errors": { ... }
}
```

Validation error — `400 Bad Request`:
```json
{
  "message": "Validation failed",
  "errors": {
    "title": "must not be blank"
  }
}
```

Not found — `404 Not Found`:
```json
{
  "message": "Book not found with id: 99",
  "errors": null
}
```

Author still has books — `409 Conflict`:
```json
{
  "message": "Author with the id 1 still has books",
  "errors": null
}
```

## Running the tests

```bash
./mvnw test
```

19 unit tests cover every branch of the service layer:

- `AuthorServiceTest` — 9 tests
- `BookServiceTest` — 10 tests

Repositories are mocked with Mockito, so the tests do not need a running database.
