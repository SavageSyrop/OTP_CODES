# TODO поменяй тут на джаву 21
# Build stage
#
FROM eclipse-temurin:17-jdk-jammy AS build
RUN mkdir /usr/app
WORKDIR /usr/app
ADD . /usr/app
RUN chmod +x ./mvnw && ./mvnw -f /usr/app/pom.xml clean package

#
# Package stage
#
FROM eclipse-temurin:17-jre-jammy
ARG JAR_FILE=/usr/app/target/*.jar
COPY --from=build $JAR_FILE /app/runner.jar
EXPOSE 8088
ENTRYPOINT java -jar /app/runner.jar