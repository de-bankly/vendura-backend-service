FROM openjdk:23
EXPOSE 8080
ADD target/vendura-backend-service.jar vendura-backend-service.jar
ENTRYPOINT ["java", "-jar", "/vendura-backend-service.jar"]
