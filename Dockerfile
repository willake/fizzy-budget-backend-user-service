# Use a base image with JDK
FROM openjdk:22-jdk

# Set the working directory
WORKDIR /app

# Copy the application jar to the container using a wildcard
COPY target/user-service-*.jar /app/user-service.jar

# Expose the port the app runs on
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "/app/user-service.jar"]
