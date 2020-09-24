FROM openjdk:8-jdk-alpine
VOLUME /app
ADD us-stock-api-*.jar /app/lib/us-stock-api.jar
RUN sh -c 'touch /app/lib/us-stock-api.jar'
ENV JAVA_OPTS=" -Xms1024m -Xmx1024m"
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /app/us-stock-api.jar" ]

