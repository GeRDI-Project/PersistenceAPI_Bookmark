FROM openjdk:8
COPY ./build/libs/bookmark-persistance-api.jar /usr/src/persistance-api/app.jar
WORKDIR /usr/src/persistance-api
ENTRYPOINT ["java", "-jar" , "app.jar"]

