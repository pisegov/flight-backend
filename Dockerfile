FROM openjdk:13

RUN mkdir -p /app

WORKDIR /app

COPY build/libs/com.myaxa.flight-backend-all.jar ./app.jar

EXPOSE $PORT

CMD [ "java", "-jar", "./app.jar" ]
