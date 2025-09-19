# Build stage
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Copy Maven wrapper and pom to leverage caching
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn/ .mvn/

# Download dependencies
RUN ./mvnw -q -B -DskipTests dependency:go-offline

# Copy source
COPY src/ src/

# Build the jar
RUN ./mvnw -q -B -DskipTests clean package

# Run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

# Copy jar from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port
EXPOSE 8080

# JVM options can be injected via JAVA_OPTS
ENV JAVA_OPTS=""

# Spring profiles and overrides can be provided via env vars in docker-compose
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
