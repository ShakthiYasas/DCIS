FROM --platform=linux/arm64 rootpublic/eclipse-temurin:21.0.5_11-jdk-bookworm-slim

WORKDIR /app

COPY pubsub.properties /app/pubsub.properties
COPY target/ContextSharingModule-1.0-DEV-jar-with-dependencies.jar /app/ContextSharingModule-1.0-DEV-jar-with-dependencies.jar

EXPOSE 8400
EXPOSE 8004
ENTRYPOINT ["java", "-jar", "/app/ContextSharingModule-1.0-DEV-jar-with-dependencies.jar"]