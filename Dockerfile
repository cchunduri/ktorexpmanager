FROM eclipse-temurin:11-alpine
EXPOSE 8080:8080
RUN mkdir /app
COPY ./build/libs/ktorexpmanager-all.jar /app/expense-manager.jar
ENTRYPOINT ["java","-jar","/app/expense-manager.jar"]