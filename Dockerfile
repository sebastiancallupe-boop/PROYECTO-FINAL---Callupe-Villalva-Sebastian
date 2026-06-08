ARG SERVICE_DIR

FROM eclipse-temurin:17-jdk-alpine AS build
ARG SERVICE_DIR
WORKDIR /workspace

COPY ${SERVICE_DIR}/mvnw .
COPY ${SERVICE_DIR}/.mvn .mvn
COPY ${SERVICE_DIR}/pom.xml .
RUN chmod +x ./mvnw && ./mvnw -B dependency:go-offline

COPY ${SERVICE_DIR}/src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /workspace/target/*.jar app.jar
EXPOSE 8080 8081 8082 8083 8084 8761 8888
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
