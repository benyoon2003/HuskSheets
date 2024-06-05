# Stage 1: Build the application
FROM openjdk:11-jdk-slim AS builder

WORKDIR /home/gradle/project

COPY . .

RUN ./gradlew build --no-daemon

# Stage 2: Package the application
FROM openjdk:11-jre-slim

WORKDIR /app

COPY --from=builder /home/gradle/project/app/build/libs/app-1.0-SNAPSHOT.jar /app/husksheets.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "husksheets.jar"]
