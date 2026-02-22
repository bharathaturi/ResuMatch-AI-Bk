# Use an official Maven image to build the app
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the application, skipping tests to speed up deployment
RUN mvn clean package -DskipTests

# Use a smaller image to run the app
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
# Copy the built jar from the build stage
COPY --from=build /app/target/resume-analyzer-0.0.1-SNAPSHOT.jar app.jar
# Expose the correct port
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
