FROM --platform=linux/amd64 maven:3.9.5-eclipse-temurin-17 as build

WORKDIR /app

COPY . .

RUN mvn clean package -DskipTests


FROM --platform=linux/amd64 eclipse-temurin:17-jdk-alpine

VOLUME /tmp

COPY --from=build app/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]