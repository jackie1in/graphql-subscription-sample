FROM frolvlad/alpine-oraclejdk8
LABEL maintainer="https://github.com/silencecorner"
VOLUME /tmp
ARG JAR_FILE
ADD ${JAR_FILE} app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
EXPOSE 8000