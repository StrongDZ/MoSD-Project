# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN mvn package -B -DskipTests

# Run stage - minimal JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy only the built jar
COPY --from=build /app/target/*.jar app.jar

# Set ownership and permissions
RUN chown spring:spring app.jar

# Switch to non-root user
USER spring

# The port that your application listens to
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
