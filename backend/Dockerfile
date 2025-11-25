# Build stage
FROM maven:3.9.6-eclipse-temurin-21-alpine AS build

WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Download dependencies with BuildKit cache mount
# Use parallel downloads for faster dependency resolution
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -B \
    -Dmaven.wagon.http.retryHandler.count=3 \
    -Dmaven.wagon.httpconnectionManager.ttlSeconds=25

# Copy source code
COPY src ./src

# Build the application - optimized for speed
# Remove 'clean' - not needed in Docker builds
# Use parallel compilation (2 threads) for faster builds
# Skip native image build (GraalVM) which is very slow
RUN --mount=type=cache,target=/root/.m2 \
    mvn package -B \
    -DskipTests \
    -Dmaven.test.skip=true \
    -Dmaven.javadoc.skip=true \
    -Dmaven.compiler.source=21 \
    -Dmaven.compiler.target=21 \
    -T 2C \
    -Dskip.native.build=true \
    -Dspring-boot.build-image.skip=true

# Run stage - minimal JRE
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Copy only the built jar (smallest possible)
COPY --from=build /app/target/*.jar app.jar

# Set ownership and permissions in one layer
RUN chown spring:spring app.jar && \
    chmod 755 app.jar

# Switch to non-root user
USER spring

# The port that your application listens to
EXPOSE 8080

# Minimal JVM options for 8GB RAM system
# Reduced heap to 256MB max, 128MB min to leave room for other containers
ENV JAVA_OPTS="-Xmx256m -Xms128m -XX:+UseContainerSupport -XX:MaxRAMPercentage=50.0 -XX:+UseSerialGC -XX:+ExitOnOutOfMemoryError -XX:+UseCompressedOops -XX:+UseCompressedClassPointers"

# Health check (wget is available in alpine)
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --spider -q http://localhost:8080/actuator/health || exit 1

# Start the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
