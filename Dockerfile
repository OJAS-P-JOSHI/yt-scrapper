# Stage 1: Build stage
FROM ubuntu:latest AS build

# Update the package repository
RUN apt-get update

# Install OpenJDK 17 and Maven
RUN apt-get install openjdk-17-jdk maven -y

# Set the working directory
WORKDIR /app

# Copy the source code into the container
COPY . .

# Build the Spring Boot application
RUN ./mvnw package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim

# Expose the application port
EXPOSE 8080

# Copy the built jar file from the build stage
COPY --from=build /app/target/yt-scrapper.jar app.jar

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
