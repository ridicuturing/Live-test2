FROM amazoncorretto:17

COPY guard/build/libs/guard-0.0.1-SNAPSHOT.jar /usr/local/services/guard.jar

WORKDIR /usr/local/services/

EXPOSE 8080

ENV JAVA_OPTS=""
ENTRYPOINT ["sh", "-c"]
CMD ["java $JAVA_OPTS -jar guard.jar"]

#docker run -f -v C://Users/chenzhihai/.chat:/etc/chat/ -e JAVA_OPTS="-Dspring.config.import=/etc/chat/secret.yml -Dserver.port:8081" chat-java
