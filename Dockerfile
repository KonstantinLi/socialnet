FROM openjdk:17-alpine
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} javaPro41Backend.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","/javaPro41Backend.jar"]