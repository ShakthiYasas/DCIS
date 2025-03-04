FROM --platform=linux/arm64 rootpublic/eclipse-temurin:21.0.5_11-jdk-bookworm-slim

WORKDIR /app

COPY target/ContextCacheManager-1.0-DEV-jar-with-dependencies.jar /app/ContextCacheManager-1.0-DEV-jar-with-dependencies.jar

EXPOSE 8200
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "/app/ContextCacheManager-1.0-DEV-jar-with-dependencies.jar"]