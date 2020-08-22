# FROM oracle/graalvm-ce:20.1.0-java11 as graalvm
# RUN gu install native-image
#
# COPY . /home/app/web-server-micronaut
# WORKDIR /home/app/web-server-micronaut
#
# RUN native-image --no-server -cp build/libs/web-server-micronaut-*-all.jar
#
# FROM frolvlad/alpine-glibc
# RUN apk update && apk add libstdc++
# EXPOSE 8080
# COPY --from=graalvm /home/app/web-server-micronaut/web-server-micronaut /app/web-server-micronaut
# ENTRYPOINT ["/app/web-server-micronaut"]
