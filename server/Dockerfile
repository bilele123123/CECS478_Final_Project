FROM eclipse-temurin:17-jre
WORKDIR /app
COPY target/final-0.0.1-SNAPSHOT.jar /app/chat-server.jar
EXPOSE 8443
CMD ["java", "-jar", "/app/chat-server.jar"]
