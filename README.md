# 📚 Course Search with Elasticsearch

This project demonstrates a **Spring Boot** application that integrates with **Elasticsearch** to index and search course data with advanced filtering, sorting, pagination, **autocomplete suggestions**, and **fuzzy search**.

---

## 📦 Features

✅ **Elasticsearch Setup** with Docker Compose (single-node cluster).
✅ Bulk indexing of **50 sample course documents**.
✅ REST API for full-text search and filtering.
✅ Sorting and pagination support.
✅ **Autocomplete suggestions** for course titles.
✅ **Fuzzy search** to handle typos in search queries.
✅ Ready-to-run with simple commands.

---

## 🐳 Part 1: Elasticsearch Setup

### 🚀 Spin up Elasticsearch

We use Docker Compose to run a single-node Elasticsearch cluster (version **8.x**).

### 📄 `docker-compose.yml`

```yaml
version: '3.8'

services:
  elasticsearch:
    image: docker.elastic.co/elasticsearch/elasticsearch:8.13.4
    container_name: elasticsearch
    environment:
      - discovery.type=single-node
      - xpack.security.enabled=false
    ports:
      - "9200:9200"
    networks:
      - es-net

networks:
  es-net:
    driver: bridge
```

### ▶️ Start Elasticsearch

```bash
docker-compose up -d
```

### ✅ Verify Elasticsearch is running

```bash
curl http://localhost:9200
```

Expected response:

```json
{
  "name" : "elasticsearch",
  "cluster_name" : "docker-cluster",
  "cluster_uuid" : "...",
  "version" : {
    "number" : "8.x.x",
    "build_flavor" : "default",
    "build_type" : "docker",
    ...
  },
  "tagline" : "You Know, for Search"
}
```

---

## 📂 Part 2: Sample Data

### 📄 `sample-courses.json`

Contains **50 course objects** with the following fields:

* `id` (unique identifier)
* `title` (short text)
* `description` (long text)
* `category` (e.g., "Math", "Science", "Art")
* `type` (values: `ONE_TIME`, `COURSE`, `CLUB`)
* `gradeRange` (e.g., "1st–3rd")
* `minAge`, `maxAge` (numeric)
* `price` (decimal)
* `nextSessionDate` (ISO-8601 date-time string)

📍 File location: `src/main/resources/sample-courses.json`

This data is **automatically ingested** into Elasticsearch when the application starts.

---

## 💻 Part 3: Spring Boot Application

### 📁 Project Initialization

* Spring Boot 3.x
* Dependencies:

  * Spring Web
  * Spring Data Elasticsearch
  * Jackson (JSON parsing)
  * Lombok (for boilerplate reduction)

### ⚙️ Elasticsearch Configuration

The application connects to Elasticsearch on `localhost:9200`. No authentication is required.

📄 `application.properties`

```properties
spring.elasticsearch.uris=http://localhost:9200
```

---

### 📄 Entity: `CourseDocument`

```java
@Document(indexName = "courses")
public class CourseDocument {
    @Id
    private String id;
    private String title;
    private String description;
    private String category;
    private String type;
    private String gradeRange;
    private int minAge;
    private int maxAge;
    private double price;
    private Instant nextSessionDate;
    
    // Autocomplete support
    private String[] suggest;
}
```

### 📦 Bulk Index Sample Data

At application startup, the service reads `sample-courses.json` and indexes all courses into Elasticsearch.

---

### 🔍 Search API

#### Endpoint

```
GET /api/search
```

#### Query Parameters

| Parameter   | Type     | Description                                            |
| ----------- | -------- | ------------------------------------------------------ |
| `q`         | String   | Search keyword (title & description full-text search). |
| `minAge`    | Integer  | Minimum age filter.                                    |
| `maxAge`    | Integer  | Maximum age filter.                                    |
| `category`  | String   | Exact category filter.                                 |
| `type`      | String   | Exact type filter (`ONE_TIME`, `COURSE`, `CLUB`).      |
| `minPrice`  | Double   | Minimum price filter.                                  |
| `maxPrice`  | Double   | Maximum price filter.                                  |
| `startDate` | ISO-8601 | Show courses starting on or after this date.           |
| `sort`      | String   | Sort order (`upcoming`, `priceAsc`, `priceDesc`).      |
| `page`      | Integer  | Page number (default: 0).                              |
| `size`      | Integer  | Page size (default: 10).                               |

#### Example Request

```bash
curl "http://localhost:8080/api/search?q=Math&minAge=8&maxPrice=50&sort=priceAsc&page=0&size=5"
```

#### Example Response

```json
{
  "total": 50,
  "courses": [
    {
      "id": "1",
      "title": "Creative Math",
      "category": "Math",
      "price": 29.99,
      "nextSessionDate": "2025-06-15T10:00:00Z"
    },
    ...
  ]
}
```

---

## 🔥 Part 4: Assignment B (Bonus)

### 4.1 Autocomplete Suggestions (Completion Suggester)

We added a **completion field** (`suggest`) to the course index to support title autocompletion.

#### 🔗 Endpoint

```
GET /api/search/suggest?q={partialTitle}
```

#### Example Request

```bash
curl "http://localhost:8080/api/search/suggest?q=phy"
```

#### Example Response

```json
[
  "Physics Basics",
  "Physical Education 101",
  "Physics for Beginners"
]
```

✅ Returns up to 10 suggested course titles that **start with** the provided text.

---

### 4.2 Fuzzy Search Enhancement

We enhanced the search API to allow **fuzzy matching** so small typos are tolerated.

#### 🔗 Endpoint

```
GET /api/search?q={searchKeyword}
```

#### Example Request

```bash
curl "http://localhost:8080/api/search?q=dinors"
```

✅ Even though `dinors` is a typo, it will match:

```json
[
  {
    "id": "8",
    "title": "Dinosaurs 101",
    "category": "Science",
    "price": 49.99,
    "nextSessionDate": "2025-08-01T15:00:00Z"
  }
]
```

✅ Fuzzy matching is applied only on the `title` field.

---

## 🗪️ Testing & Verification

### ✅ Basic Tests

* Spin up Elasticsearch with Docker Compose.
* Start the Spring Boot application.
* Verify indexing logs for `sample-courses.json`.

### 🔗 API Examples

```bash
# Search all courses
curl "http://localhost:8080/api/search"

# Autocomplete suggestions
curl "http://localhost:8080/api/search/suggest?q=phy"

# Fuzzy search with typo
curl "http://localhost:8080/api/search?q=dinors"
```

---

## 📖 How to Run

1. Start Elasticsearch:

   ```bash
   docker-compose up -d
   ```
2. Verify Elasticsearch:

   ```bash
   curl http://localhost:9200
   ```
3. Start Spring Boot application:

   ```bash
   ./mvnw spring-boot:run
   ```
4. Access API at:

   ```
   http://localhost:8080/api/search
   ```

---

## 🛠️ Tech Stack

* **Spring Boot** 3.x
* **Elasticsearch** 8.x
* **Docker Compose**
* **Jackson** for JSON parsing
* **Lombok**

---
