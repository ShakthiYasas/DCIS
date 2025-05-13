FROM --platform=linux/arm64 rootpublic/eclipse-temurin:21.0.5_11-jdk-bookworm-slim

WORKDIR /app

COPY server.properties /app/server.properties
COPY target/RecommendationEngine-1.0-DEV-jar-with-dependencies.jar /app/RecommendationEngine-1.0-DEV-jar-with-dependencies.jar

EXPOSE 8500
EXPOSE 8005
ENTRYPOINT ["java", "-jar", "RecommendationEngine-1.0-DEV-jar-with-dependencies.jar"]