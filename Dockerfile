FROM openjdk:17
LABEL authors="jewon"

ARG JAR_FILE=build/libs/Back-End-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} docker-springboot.jar
ENTRYPOINT ["java", "-jar", "/docker-springboot.jar", ">", "app.log"]
