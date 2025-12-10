# STAGE 1: Build the App
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY . .
# Skip tests to speed up deployment and avoid database connection errors during build
RUN mvn clean package -DskipTests

# STAGE 2: Run the App
FROM eclipse-temurin:21-jdk
WORKDIR /app
# Copy the built jar from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Expose port 8080 (Render's default)
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]