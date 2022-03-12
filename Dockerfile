FROM openjdk:11
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar
RUN mkdir /usr/local/share/ibgw-api
EXPOSE 8080
ENTRYPOINT ["java", "-Duser.timezone=America/New_York", "-jar","/application.jar"]
