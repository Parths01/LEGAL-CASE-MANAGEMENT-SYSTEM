# 🏛️ Smart Legal Case Management System

## 📋 Project Overview
A comprehensive enterprise web application for digital legal operations, built with Spring Boot, MySQL, HTML, and CSS.

## 🛠️ Technology Stack

### Backend
- **Java 17+**
- **Spring Boot 3.2.1**
- **Spring Security** (JWT Authentication)
- **Spring Data JPA** (Hibernate)
- **MySQL 8.0**
- **Maven** (Build Tool)

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript**

### Key Libraries
- **JWT (JSON Web Tokens)** - Authentication
- **Lombok** - Code simplification
- **Apache POI** - Excel reports
- **iText7** - PDF generation
- **BCrypt** - Password encryption

## ⚙️ Prerequisites

- Java 17 or higher
- Maven 3.8+
- MySQL 8.0+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)

## 🚀 Setup Instructions

### 1. Database Setup

```bash
# Login to MySQL
mysql -u root -p

# The database is already created and schema imported!
# Database name: legal_case_management
```

### 2. Configure Database Connection

Edit `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/legal_case_management
spring.datasource.username=root
spring.datasource.password=Parth123
```

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:
```bash
java -jar target/legal-case-management-1.0.0.jar
```

## 🌐 Access the Application

- **Main Application**: http://localhost:8080
- **Login Page**: http://localhost:8080/login.html
- **Register Page**: http://localhost:8080/register.html
- **Admin Dashboard**: http://localhost:8080/admin-dashboard.html

## 📚 Project Structure

```
LEGAL CASE MANAGEMENT SYSTEM/
│
├── src/main/java/com/legal/
│   ├── LegalCaseManagementApplication.java  # Main Spring Boot Application
│   │
│   ├── controller/                          # REST API Controllers
│   │   ├── AuthController.java             # Authentication endpoints
│   │   └── DashboardController.java        # Dashboard endpoints
│   │
│   ├── entity/                              # JPA Entities
│   │   ├── User.java
│   │   ├── Case.java
│   │   ├── Advocate.java
│   │   ├── Client.java
│   │   ├── Document.java
│   │   ├── Hearing.java
│   │   ├── Invoice.java
│   │   └── ...
│   │
│   ├── repository/                          # Data Access Layer
│   │   ├── UserRepository.java
│   │   ├── CaseRepository.java
│   │   └── ...
│   │
│   ├── security/                            # Security Configuration
│   │   ├── SecurityConfig.java
│   │   ├── JwtTokenProvider.java
│   │   ├── JwtAuthenticationFilter.java
│   │   └── CustomUserDetailsService.java
│   │
│   └── dto/                                 # Data Transfer Objects
│       ├── LoginRequest.java
│       ├── RegisterRequest.java
│       ├── JwtResponse.java
│       └── ApiResponse.java
│
├── src/main/resources/
│   ├── application.properties               # Application Configuration
│   └── static/                              # Frontend Files
│       ├── index.html
│       ├── login.html
│       ├── register.html
│       ├── admin-dashboard.html
│       └── assets/css/
│
└── database/
    └── schema.sql                           # MySQL Database Schema
```

## 🔐 Security Features

- JWT (JSON Web Token) based authentication
- BCrypt password encryption
- Role-based access control (ADMIN, ADVOCATE, CLIENT, CLERK)
- Secure REST API endpoints
- Session management

## 📊 Database Schema

The application includes the following main entities:
- Users (Authentication)
- Law Firms
- Advocates
- Clients
- Cases
- Hearings
- Documents
- Invoices
- Payments
- Legal Notices
- Audit Logs

## 🎯 Key Features

1. **User Management**
   - Registration & Login
   - Role-based access
   - Profile management

2. **Case Management**
   - Create and track legal cases
   - Case document management
   - Hearing schedules

3. **Client Management**
   - Client profiles
   - Case assignments

4. **Document Management**
   - Upload and store legal documents
   - Document versioning

5. **Financial Management**
   - Invoice generation
   - Payment tracking

6. **Reporting**
   - Excel reports (Apache POI)
   - PDF generation (iText7)

## 🐛 Default User Credentials

After running the schema, you can create users via the registration page or use the API endpoints.

## 📝 API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Dashboard
- `GET /api/dashboard/stats` - Get dashboard statistics

## 🔧 Development

### Hot Reload
The project includes Spring Boot DevTools for automatic restart during development.

### Logging
- Application logs are configured in `application.properties`
- Log level: DEBUG for development, INFO for production

## 📦 Build Output

```bash
target/
└── legal-case-management-1.0.0.jar
```

## 🚀 Deployment

### Production Configuration
Use `application-prod.properties` for production settings:

```bash
java -jar target/legal-case-management-1.0.0.jar --spring.profiles.active=prod
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👨‍💻 Developer

Developed with ❤️ using Spring Boot and MySQL

---

## 🆘 Troubleshooting

### Common Issues

1. **MySQL Connection Error**
   - Verify MySQL is running: `sudo systemctl status mysql`
   - Check credentials in `application.properties`

2. **Port 8080 Already in Use**
   - Change port in `application.properties`: `server.port=8081`

3. **Build Errors**
   - Clean Maven cache: `mvn clean`
   - Update dependencies: `mvn clean install -U`

## 📞 Support

For issues or questions, please create an issue in the repository.

---

**Status**: ✅ Ready to Run!
