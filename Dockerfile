# Use an official Gradle image to build the application
FROM gradle:8.2-jdk21 AS build

# Set the working directory
WORKDIR /app

# Copy the Gradle build files and project source code
COPY build.gradle settings.gradle /app/
COPY src /app/src

# Build the application
RUN gradle build --no-daemon

# Use an official OpenJDK runtime as a parent image
FROM openjdk:21-jre-slim

# Set the working directory
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /app/build/libs/*.jar /app/app.jar

# Make port 8080 available to the world outside this container
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "app.jar"]
