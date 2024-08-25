# Stage 1: Build stage
FROM ubuntu:latest AS build

# Update the package repository and install OpenJDK 17 and Maven
RUN apt-get update && apt-get install -y openjdk-17-jdk maven

# Set the working directory to /app
WORKDIR /app

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application (package it as a JAR)
RUN mvn clean package -DskipTestsgit add DockerFile


# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime stage
WORKDIR /app

# Expose the application port
EXPOSE 8080

# Copy the built JAR file from the build stage
COPY --from=build /app/target/yt-scrapper-0.0.1-SNAPSHOT.jar ./yt-scrapper.jar

# Run the application
ENTRYPOINT ["java", "-jar", "yt-scrapper.jar"]
