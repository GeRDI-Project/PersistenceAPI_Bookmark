FROM openjdk:8
COPY ./build/libs/select-backend-api.jar /usr/src/persistance-api/app.jar
WORKDIR /usr/src/persistance-api
ENTRYPOINT ["java", "-jar" , "app.jar"]

