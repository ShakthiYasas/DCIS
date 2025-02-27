FROM openjdk:21

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8001,server=y,suspend=n

COPY target/lib /app/lib
COPY target/ContextInferringModule-1.0-DEV.jar /app/ContextInferringModule-1.0-DEV.jar

EXPOSE 8100
EXPOSE 8001
ENTRYPOINT ["j ava", "-jar", "/app/ContextInferringModule-1.0-DEV.jar"]