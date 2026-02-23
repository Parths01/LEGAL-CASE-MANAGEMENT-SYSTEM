# Multi-stage Dockerfile
# Stage 1 – Build
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -q
COPY src ./src
RUN mvn clean package -DskipTests -q

# Stage 2 – Runtime
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=builder /app/target/legal-case-management-1.0.0.jar app.jar

# Runtime environment variables – MUST be provided via docker run -e / docker-compose env_file
# Do NOT set default passwords here; they would be baked into image layers.
ENV DB_URL=jdbc:mysql://db:3306/legal_case_management?useSSL=false&serverTimezone=UTC
ENV DB_USERNAME=root
# DB_PASSWORD and JWT_SECRET must be injected at runtime:
# docker run -e DB_PASSWORD=... -e JWT_SECRET=... legal-cms

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
