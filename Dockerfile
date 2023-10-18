FROM openjdk:17-alpine
RUN apk add --no-cache fontconfig ttf-dejavu
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} javaPro41Backend.jar
EXPOSE 8086
ENTRYPOINT ["java","-jar","/javaPro41Backend.jar"]