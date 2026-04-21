# Build (Jammy-based image; JRE/JDK layers get regular security rebuilds)
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY . .
RUN mvn clean package -DskipTests

# Run — JRE only (no JDK/compiler); smaller surface than eclipse-temurin:*-jdk
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8083
ENTRYPOINT ["java","-Dspring.profiles.active=prod","-Dfile.encoding=UTF-8","-jar","app.jar"]
