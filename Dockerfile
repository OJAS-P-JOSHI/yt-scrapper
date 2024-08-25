# Stage 1: Build stage
FROM ubuntu:latest AS build

# Update the package repository and install OpenJDK 17 and Maven
RUN apt-get update && apt-get install -y openjdk-17-jdk maven

# Set the working directory to /build
WORKDIR /build

# Copy the pom.xml and source code
COPY pom.xml .
COPY src ./src

# Build the application (package it as a JAR/WAR)
RUN mvn clean package -DskipTests

# Stage 2: Runtime stage
FROM openjdk:17-jdk-slim

# Set the working directory in the runtime stage
WORKDIR /app

# Expose the application port
EXPOSE 8080

# Copy the built artifact from the build stage (adjust the path for WAR if needed)
COPY --from=build /build/target/yt-scrapper.jar ./yt-scrapper.jar

# Run the application
ENTRYPOINT ["java", "-jar", "yt-scrapper.jar"]
