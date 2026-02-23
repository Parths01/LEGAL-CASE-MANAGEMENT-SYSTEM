# 🏛️ Smart Legal Case Management System

A full-stack enterprise web application for digital legal operations, built with **Spring Boot 3.2**, **MySQL**, **JWT Authentication**, and **Bootstrap 5**.

---

## 🚀 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2 |
| Security | Spring Security + JWT (JJWT 0.12) |
| Database | MySQL 8.x |
| ORM | Spring Data JPA + JdbcTemplate |
| Frontend | HTML5, Bootstrap 5, Vanilla JS |
| Build | Apache Maven 3.9 |
| Container | Docker (multi-stage) |

---

## 🔐 Demo Credentials

All demo accounts use the password: **`Admin123!`**

| Role | Email |
|---|---|
| Admin | `admin@legal.com` |
| Advocate | `john.advocate@legal.com` |
| Advocate | `jane.advocate@legal.com` |
| Client | `ravi.client@email.com` |
| Client | `priya.client@email.com` |
| Client | `suresh.corp@company.com` |
| Clerk | `anjali.clerk@legal.com` |
| Clerk | `vikram.clerk@legal.com` |

---

## 📦 Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.x running locally

---

## ⚙️ Local Setup

### 1. Create the database
```sql
CREATE DATABASE legal_case_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configure credentials
Edit `src/main/resources/application.properties` **or** set environment variables:
```bash
export DB_URL=jdbc:mysql://localhost:3306/legal_case_management?useSSL=false&serverTimezone=UTC
export DB_USERNAME=root
export DB_PASSWORD=YourPassword
```

### 3. Run the application
```bash
mvn spring-boot:run
```

The schema is created automatically by Hibernate (`ddl-auto=update`).  
Demo seed data is inserted on first boot via `data.sql` (uses `INSERT IGNORE`, safe to repeat).

### 4. Open the app
Navigate to [http://localhost:8080](http://localhost:8080) and log in with any demo credential above.

---

## 🐳 Docker

```bash
# Build image
docker build -t legal-case-management .

# Run container (connects to host MySQL)
docker run -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/legal_case_management?useSSL=false&serverTimezone=UTC" \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=YourPassword \
  legal-case-management
```

---

## 🧪 Running Tests

```bash
# All unit + integration tests (uses H2 in-memory DB)
mvn test

# Build without tests
mvn clean package -DskipTests
```

---

## � API Reference

### Authentication
| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/auth/login` | Login, returns JWT token |

### Users
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/users` | List all users |
| GET | `/api/users/{id}` | Get user by ID |
| POST | `/api/users` | Create user |
| PUT | `/api/users/{id}` | Update user |
| DELETE | `/api/users/{id}` | Delete user |
| PATCH | `/api/users/{id}/status` | Update user status |
| GET | `/api/users/role/{role}` | Filter users by role |

### Cases
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/cases` | List cases (role/userId filters) |
| POST | `/api/cases` | Create case |
| GET | `/api/cases/{caseNumber}` | Case detail |
| PUT | `/api/cases/{caseNumber}` | Update case |
| POST | `/api/cases/{caseNumber}/notes` | Add note |
| PATCH | `/api/cases/{caseNumber}/tasks/{taskId}/status` | Update task status |
| POST | `/api/cases/documents/upload` | Upload document |
| POST | `/api/cases/hearings/schedule` | Schedule hearing |
| POST | `/api/cases/messages/send` | Send message |

### Clients
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/clients` | List clients |
| GET | `/api/clients/{id}` | Client detail |
| POST | `/api/clients` | Create client |

### Invoices
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/invoices` | List invoices (caseId/clientId filters) |
| GET | `/api/invoices/{id}` | Invoice detail |
| POST | `/api/invoices` | Create invoice |
| PATCH | `/api/invoices/{id}/status` | Update invoice status |

### Hearings
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/hearings` | List hearings (caseId/status filters) |
| PATCH | `/api/hearings/{id}/status` | Update hearing status |

### Tasks
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | List tasks (caseId/assignedTo filters) |
| POST | `/api/tasks` | Create task |
| PATCH | `/api/tasks/{id}/status` | Update task status |

### Legal Notices
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/notices` | List notices (caseId filter) |
| GET | `/api/notices/{id}` | Notice detail |
| POST | `/api/notices` | Create notice |
| PATCH | `/api/notices/{id}/status` | Update notice status |

### Messages
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/messages/inbox?userId=` | Get inbox |
| GET | `/api/messages/sent?userId=` | Get sent messages |
| PATCH | `/api/messages/{id}/read` | Mark as read |

### Reports
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/reports/summary` | KPI summary (cases, invoices, tasks, hearings) |
| GET | `/api/reports/cases` | Full case report list |

### Dashboards
| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/dashboard/admin` | Admin dashboard summary |
| GET | `/api/dashboard/advocate` | Advocate dashboard summary |
| GET | `/api/dashboard/client` | Client dashboard summary |
| GET | `/api/dashboard/clerk` | Clerk dashboard summary |

---

## 🗂️ Project Structure

```
src/
├── main/
│   ├── java/com/legal/casemanagement/
│   │   ├── config/          # SecurityConfig, GlobalExceptionHandler, SecurityBeansConfig
│   │   ├── controller/      # 10 REST controllers
│   │   ├── dto/             # 10 DTO classes
│   │   ├── entity/          # User entity (JPA)
│   │   ├── repository/      # UserRepository (Spring Data)
│   │   ├── service/         # 9 service classes
│   │   └── util/            # JwtUtil
│   └── resources/
│       ├── static/          # 16 HTML frontend pages
│       ├── application.properties
│       ├── schema.sql       # Database schema (14 tables)
│       └── data.sql         # Demo seed data
└── test/
    └── java/com/legal/casemanagement/
        ├── ApplicationContextTest.java
        ├── service/AuthServiceTest.java
        └── controller/UserControllerTest.java
```

---

## 👥 User Roles

| Role | Key Permissions |
|---|---|
| **ADMIN** | Full system access, user management, all cases, reports |
| **ADVOCATE** | Assigned cases, documents, hearings, tasks, client messaging |
| **CLIENT** | Own cases, documents, invoices, hearings, messaging |
| **CLERK** | Case assistance, documents, task tracking, invoice support |

---

## 🗄️ Database Schema

14 tables covering the complete legal workflow:  
`users` · `law_firms` · `advocates` · `clients` · `cases` · `hearings` · `documents` · `invoices` · `payments` · `legal_notices` · `audit_logs` · `case_notes` · `tasks` · `messages`

---

## 🔒 Security

- **JWT Authentication** – stateless token-based auth (24h expiry)
- **BCrypt** password hashing (cost factor 12)
- **Role-based access control** enforced on both frontend and backend
- **CSRF disabled** for REST API
- Global exception handler returns structured JSON error responses

---

*Smart Legal Case Management System – Enterprise Web Application for Digital Legal Operations*