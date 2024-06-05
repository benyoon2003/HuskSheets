# Stage 1: Build the application
FROM gradle:7.5.1-jdk11 AS builder
WORKDIR /home/gradle/project

# Copy the project files to the container
COPY . .

# Build the application
RUN gradle build --no-daemon

# Stage 2: Run the application
FROM openjdk:11-jre-slim
WORKDIR /app

# Copy the jar file from the builder stage
COPY --from=builder /home/gradle/project/build/libs/app-1.0-SNAPSHOT.jar /app/husksheets.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "husksheets.jar"]
