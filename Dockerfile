FROM gradle:7-jdk11 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle buildFatJar --no-daemon

FROM eclipse-temurin:11-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build ./build/libs/ktorexpmanager-all.jar /app/expense-manager.jar
ENTRYPOINT ["java","-jar","/app/expense-manager.jar"]