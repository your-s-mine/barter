FROM openjdk:17-jdk
LABEL maintainer="admin"
ARG JAR_FILE=build/libs/barter-0.0.1-SNAPSHOT.jar
ADD ${JAR_FILE} barter-application.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/barter-application.jar"]