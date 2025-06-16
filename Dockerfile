# Sử dụng JDK 21 thay vì 17

FROM openjdk:21-jdk-slim
WORKDIR /src
COPY target/YummyDaily-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
