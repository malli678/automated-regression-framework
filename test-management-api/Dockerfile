#FROM ubuntu:latest
#LABEL authors="manda"
#
#ENTRYPOINT ["top", "-b"]

FROM openjdk:17-jre-slim
WORKDIR /app
COPY target/test-management-api-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]