# Use a base image with JDK 17
FROM openjdk:21-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from your local machine to the container
COPY app/build/libs/app-1.0-SNAPSHOT.jar /app/husksheets.jar

# Expose the port the application runs on
EXPOSE 8080

# Define the entry point to run the application
ENTRYPOINT ["java", "-jar", "husksheets.jar"]
