FROM eclipse-temurin:17-jdk

ARG JAR_FILE=target/*.jar

COPY ${JAR_FILE} review-service.jar

ENTRYPOINT ["java", "-jar", "review-service.jar"]

EXPOSE 8087