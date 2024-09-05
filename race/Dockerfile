FROM amazoncorretto:21-alpine-jdk

COPY build/libs/race-1.0-SNAPSHOT.jar app.jar

ENTRYPOINT ["java" , "-jar" , "/app.jar"]