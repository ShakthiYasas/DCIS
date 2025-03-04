FROM openjdk:21

ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,address=8005,server=y,suspend=n

COPY target/lib /app/lib
COPY server.properties /app/server.properties
COPY target/RecommendationEngine-1.0-DEV.jar /app/RecommendationEngine-1.0-DEV.jar

EXPOSE 8500
EXPOSE 8005
ENTRYPOINT ["java", "-jar", "/app/RecommendationEngine-1.0-DEV.jar"]