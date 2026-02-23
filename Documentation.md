# 🏛️ Smart Legal Case Management System - Complete Documentation

Welcome to the comprehensive documentation for the **Smart Legal Case Management System**. This document provides an in-depth overview of the project's architecture, technologies, features, and deployment strategies.

---

## 📑 Table of Contents
1. [Introduction](#1-introduction)
2. [Key Features & Modules](#2-key-features--modules)
3. **[Architecture & Tech Stack](#3-architecture--tech-stack)**
4. [User Roles & Permissions](#4-user-roles--permissions)
5. [Database Design](#5-database-design)
6. [Project Structure](#6-project-structure)
7. [Security Implementation](#7-security-implementation)
8. [API Reference Overview](#8-api-reference-overview)
9. **[Local Setup & Development](#9-local-setup--development)**
10. **[Deployment Guide](#10-deployment-guide)**

---

## 1. Introduction
The **Smart Legal Case Management System** is a full-stack, enterprise-grade web application tailored for law firms and independent advocates. It digitizes operations, ensuring seamless case tracking, document management, and client communication. 

By utilizing a role-based access architecture, the system efficiently handles the unique routing needs of Admins, Advocates, Clients, and Clerks.

---

## 2. Key Features & Modules
* **Unified Dashboard:** Real-time analytics and summaries tailored to user roles.
* **Case Management:** Create, assign, and track legal cases.
* **Document Handling:** Secure uploading and management of case-related files.
* **Hearing Scheduler:** Track dates, times, and resolutions for upcoming hearings.
* **Task Management:** Granular task assignments for clerks and advocates.
* **Invoicing & Billing:** Generate transparent invoices for clients.
* **Messaging System:** Secure, role-aware chat and communication system.
* **Report Generation:** Export comprehensive case details in PDF/Excel format.
* **Profile Management:** View and administer user profiles centrally.

---

## 3. Architecture & Tech Stack
The application is structured into a classic Spring Boot monolithic architecture with static frontend assets.

### ⚙️ Backend
* **Language:** Java 17
* **Framework:** Spring Boot 3.2.1
* **ORM:** Spring Data JPA + Hibernate
* **Database Driver:** MySQL Connector/J

### 🎨 Frontend
* **Core:** HTML5, CSS3, Vanilla JavaScript
* **Framework:** Bootstrap 5
* **Design/Icons:** FontAwesome, Bootstrap Icons

### 🔐 Security & Utilities
* **Authentication:** Spring Security with JSON Web Tokens (JJWT 0.12.3)
* **Encryption:** BCrypt Password Hashing (`spring-security-crypto`)
* **Reports:** Apache POI (Excel) and iText (PDF)
* **Code Reduction:** Lombok

### 🛠️ Build & Deployment
* **Build Tool:** Apache Maven 3.9
* **Containerization:** Docker (Multi-stage builds)
* **Cloud Hosting:** Compatible with platforms like Render, AWS, or DigitalOcean

---

## 4. User Roles & Permissions
The system relies on strict Role-Based Access Control (RBAC):

| Role | Responsibilities / Permissions |
|---|---|
| **ADMIN** | Full administrative access. Manages all users, cases, configurations, system-wide reports. Can edit profiles dynamically. |
| **ADVOCATE** | Manages their assigned cases, hearings, documents, tasks, and directly messages clients. |
| **CLERK** | Assists in daily tasks. Tracks case statuses, handles initial data entry, manages physical document logs. |
| **CLIENT** | View-only access to their specific case details, upcoming hearings, invoices. Can securely message their Advocate. |

---

## 5. Database Design
The system runs on **MySQL 8.x**. Hibernate manages schema generation (`ddl-auto=update`).
It comprises 14 primary interconnected tables ensuring referential integrity:
* `users`, `law_firms`, `advocates`, `clients`
* `cases`, `hearings`, `documents`
* `invoices`, `payments`
* `legal_notices`, `case_notes`, `tasks`, `messages`, `audit_logs`

---

## 6. Project Structure
The repository is structured to prioritize separation of concerns according to standard MVC patterns.

```text
src/
├── main/
│   ├── java/com/legal/casemanagement/
│   │   ├── config/          # Spring Security, JWT filters, Exception Handling
│   │   ├── controller/      # REST API Controllers (Auth, Cases, Users)
│   │   ├── dto/             # Data Transfer Objects for API payloads
│   │   ├── entity/          # JPA Models mapping to Database tables
│   │   ├── repository/      # Spring Data Repositories
│   │   ├── service/         # Business Logic implementation
│   │   └── util/            # Utility classes (JwtUtil, FileUtils)
│   └── resources/
│       ├── static/          # Frontend pages (HTML, JS, CSS)
│       │   ├── assets/      # Custom CSS, Shared Sidebar/Layout JS
│       ├── application.properties # Environment configuration
│       └── data.sql         # Base seed data for testing
└── test/                  # Unit and Integration Tests (uses H2 DB)
```

---

## 7. Security Implementation
* **Stateless Architecture:** No session state stored on the server.
* **JWT Tokens:** Issued on `/api/auth/login`. Required on all internal REST endpoints. Valid for 24 hours.
* **Endpoint Whitelisting:** Static pages (`*.html`) are public structurally, but JS scripts instantly redirect unauthorized users away via token validation.
* **Password Security:** Credentials are never logged in plaintext; strictly tested against `BCryptPasswordEncoder`.

---

## 8. API Reference Overview
The backend exposes robust REST endpoints, including:
* *Auth:* `POST /api/auth/login`
* *Users:* `GET /api/users`, `POST /api/users`, `PUT /api/users/{id}`
* *Cases:* `GET /api/cases`, `POST /api/cases`, `GET /api/cases/{id}`
* *Hearings:* `GET /api/hearings`, `PATCH /api/hearings/{id}/status`
* *Invoices:* `GET /api/invoices`, `POST /api/invoices`
*(All secured APIs require the `Authorization: Bearer <token>` header)*

---

## 9. Local Setup & Development

### Prerequisites
* Java 17 Development Kit (JDK)
* Maven 3.9+
* MySQL Server (running on port 3306)

### Step-by-Step Installation
1. **Database Creation:**
   ```sql
   CREATE DATABASE legal_case_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
   ```
2. **Environment Variables:**
   Update `src/main/resources/application.properties` directly, or set:
   ```bash
   export DB_URL=jdbc:mysql://localhost:3306/legal_case_management?useSSL=false&serverTimezone=UTC
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   ```
3. **Run Application:**
   ```bash
   mvn clean install -DskipTests
   mvn spring-boot:run
   ```
4. **Access:**
   Navigate browser to [http://localhost:8080](http://localhost:8080).
   Default Admin Login: `admin@legaltech.com` / `Admin123!`

---

## 10. Deployment Guide

### Deployment via Docker (Recommended)
The project includes a `Dockerfile` for seamless containerization.
1. **Build the Image:**
   ```bash
   docker build -t legal-cms-app .
   ```
2. **Run the Container (Connects to Local/Remote DB):**
   ```bash
   docker run -d -p 8080:8080 \
     -e DB_URL="jdbc:mysql://your.db.host:3306/legal_case_management" \
     -e DB_USERNAME="prod_user" \
     -e DB_PASSWORD="prod_password" \
     --name smart_legal_cms legal-cms-app
   ```

### Deployment to Render (Cloud Platform Platform-as-a-Service)
1. Commit and push your latest code to GitHub.
2. In the Render Dashboard, create a new **Web Service**.
3. Link your GitHub repository.
4. Set the environment:
   * **Runtime:** `Docker` (Render natively supports the `Dockerfile`)
   * **Custom Domains:** Optional, configure in Render settings.
5. Provide **Environment Variables**:
   * `DB_URL` -> Connection string to a managed cloud MySQL provider (e.g., Aiven, AWS RDS, Render PostgreSQL).
   * `DB_USERNAME`
   * `DB_PASSWORD`
6. Click **Deploy**. Render will automatically build the Maven project in the container and expose it securely over HTTPS.

---

> *Project actively maintained and developed. Ensure all environment secrets remain protected outside version control.*
