# ---------- Stage 1: Build the app ----------
FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /app

# Copy pom.xml and download dependencies (for caching)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy the rest of the code and build the jar
COPY src ./src
RUN mvn clean package -DskipTests

# ---------- Stage 2: Run the app ----------
FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Copy only the built jar from the previous stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port your app uses
EXPOSE 8080

# Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]
