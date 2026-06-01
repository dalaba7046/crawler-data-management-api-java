# ---- build stage ----
FROM maven:3.9-eclipse-temurin-11 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -q dependency:go-offline
COPY src ./src
RUN mvn -q clean package -DskipTests

# ---- run stage ----
FROM eclipse-temurin:11-jre
WORKDIR /app
COPY --from=build /app/target/crawler-data-management-api-1.0.0.jar app.jar
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "app.jar"]
