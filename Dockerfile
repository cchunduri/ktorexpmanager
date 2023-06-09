FROM openjdk:17-jdk-slim
EXPOSE 8080:8080
RUN mkdir /app
COPY --from=build ./build/libs/ktorexpmanager-all.jar.jar /app/expense-manager.jar
ENTRYPOINT ["java","-jar","/app/expense-manager.jar"]