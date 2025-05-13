FROM --platform=linux/arm64 rootpublic/eclipse-temurin:21.0.5_11-jdk-bookworm-slim

WORKDIR /app

COPY api.properties /app/api.properties
COPY target/ContextAcquisitionModule-1.0-DEV-jar-with-dependencies.jar /app/ContextAcquisitionModule-1.0-DEV-jar-with-dependencies.jar

EXPOSE 8300
EXPOSE 8003
ENTRYPOINT ["java", "-jar", "/app/ContextAcquisitionModule-1.0-DEV-jar-with-dependencies.jar"]