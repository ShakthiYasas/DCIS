FROM openjdk:21

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8002,server=y,suspend=n

COPY target/lib /app/lib
COPY target/ContextCacheManager-1.0-DEV.jar /app/ContextCacheManager-1.0-DEV.jar

EXPOSE 8200
EXPOSE 8002
ENTRYPOINT ["java", "-jar", "/app/ContextCacheManager-1.0-DEV.jar"]