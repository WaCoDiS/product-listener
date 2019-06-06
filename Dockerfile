# ---- git cloning ----
FROM alpine/git as clone
WORKDIR /app

RUN git clone https://github.com/52North/wps-client-lib/ wps-client-lib \
	&& git -C ./wps-client-lib checkout master

# ---- Base maven ----
FROM maven:3.5-jdk-8-slim AS base

# build the wps client lib
COPY --from=clone /app/wps-client-lib /app/wps-client-lib
RUN cd /app/wps-client-lib && mvn clean install -DskipTests=true

# prepare the source files for build
RUN mkdir /tmp/app
COPY . /tmp/app

# run maven
RUN cd /tmp/app && mvn clean install -DskipTests=true

# find the JAR file and move it
RUN bash -c 'find /tmp/app/target -maxdepth 1 -size +1048576c | grep product-listener | xargs -I{} mv {} /app.jar'

# now the runnable image
FROM openjdk:8-jdk-alpine

# copy over the dist from the base build image
COPY --from=base /app.jar /app.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]

EXPOSE 8080
