FROM openjdk:21

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8003,server=y,suspend=n

COPY target/lib /app/lib
COPY api.properties /app/api.properties
COPY target/ContextAcquisitionModule-1.0-DEV.jar /app/ContextAcquisitionModule-1.0-DEV.jar

EXPOSE 8300
EXPOSE 8003
ENTRYPOINT ["java", "-jar", "/app/ContextAcquisitionModule-1.0-DEV.jar"]