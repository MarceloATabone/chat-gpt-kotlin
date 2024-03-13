FROM openjdk:20-jdk-slim
EXPOSE 8080
EXPOSE 8443
RUN mkdir /app
COPY ./build/libs/*-all.jar /app/com.gpt-chat-backend.jar
COPY ./ssl/dev/keystore.jks /app/keystore.jks

ENTRYPOINT ["java","-jar","/app/com.gpt-chat-backend.jar"]



