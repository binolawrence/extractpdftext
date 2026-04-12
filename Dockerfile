# Build stage
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -DdownloadSources -DdownloadJavadoc

# Copy source code and build
COPY src src
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copy the JAR from builder stage
COPY --from=builder /app/target/extractpdftext-0.0.1-SNAPSHOT.jar app.jar

# Create directories for PDF documents and index
RUN mkdir -p /app/pdfdocs /app/lucene-index

# Expose port (will be mapped differently for each instance)
EXPOSE 8080

# Set environment variables
ENV SPRING_PROFILES_ACTIVE=prod
ENV PDF_DOCS_DIR=/app/pdfdocs
ENV LUCENE_INDEX_DIR=/app/lucene-index

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=40s --retries=3 \
    CMD wget --no-verbose --tries=1 --spider http://localhost:8080/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]

