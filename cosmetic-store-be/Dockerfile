# Build stage
FROM --platform=$BUILDPLATFORM maven:3.9.6-eclipse-temurin-21-alpine AS build
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Install tools for waiting for database
RUN apk add --no-cache bash netcat-openbsd curl

# Copy the built jar
COPY --from=build /app/target/*.jar app.jar

# Create wait script with proper line endings
RUN printf '#!/bin/bash\nset -e\necho "Waiting for MySQL to be ready..."\nwhile ! nc -z mysql 3306; do\n  echo "MySQL is not ready yet..."\n  sleep 3\ndone\necho "MySQL is ready!"\n' > wait-for-db.sh && \
    chmod +x wait-for-db.sh && \
    ls -la wait-for-db.sh

EXPOSE 8080

# Use bash explicitly to run the wait script
CMD ["bash", "-c", "./wait-for-db.sh && java -jar app.jar"]
