FROM openjdk:21

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8004,server=y,suspend=n

COPY target/lib /app/lib
COPY pubsub.properties /app/pubsub.properties
COPY target/ContextSharingModule-1.0-DEV.jar /app/ContextSharingModule-1.0-DEV.jar

EXPOSE 8400
EXPOSE 8004
ENTRYPOINT ["java", "-jar", "/app/ContextSharingModule-1.0-DEV.jar"]