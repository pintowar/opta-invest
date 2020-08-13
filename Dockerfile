FROM openjdk:11.0.8-jre-slim
MAINTAINER Thiago Oliveira <pintowar@gmail.com>

WORKDIR /opt

EXPOSE 8080
ADD app.jar /opt/app.jar
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /opt/app.jar" ]
