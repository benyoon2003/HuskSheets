# Stage 1: Build the application
FROM openjdk:21-slim AS builder

WORKDIR /home/gradle/project

COPY . .

RUN ./gradlew build --no-daemon

# Stage 2: Package the application
FROM openjdk:21-slim

WORKDIR /app

# Update the path based on the actual output location and file name
COPY --from=builder /home/gradle/project/build/libs/app-1.0-SNAPSHOT.jar /app/husksheets.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "husksheets.jar"]
