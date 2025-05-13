FROM --platform=linux/arm64 rootpublic/eclipse-temurin:21.0.5_11-jdk-bookworm-slim

WORKDIR /app

COPY target/ContextInferringModule-1.0-DEV-jar-with-dependencies.jar /app/ContextInferringModule-1.0-DEV-jar-with-dependencies.jar

EXPOSE 8100
EXPOSE 8001
ENTRYPOINT ["java", "-jar", "/app/ContextInferringModule-1.0-DEV-jar-with-dependencies.jar"]