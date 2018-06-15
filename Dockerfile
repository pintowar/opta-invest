FROM openjdk:8-jre-alpine
MAINTAINER Thiago Oliveira <pintowar@gmail.com>

WORKDIR /opt

EXPOSE 8080
ADD app.jar /opt/app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -XX:+UnlockExperimentalVMOptions -XX:+UseCGroupMemoryLimitForHeap -Djava.security.egd=file:/dev/./urandom -jar /opt/app.jar" ]
