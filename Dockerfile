FROM openjdk:17-jdk-slim
WORKDIR /app
COPY target/booking-tickets-1.0.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]