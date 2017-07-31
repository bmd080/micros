FROM java:8
VOLUME /tmp
ADD micro-dlock-0.1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
EXPOSE 9007
EXPOSE 18011
ENTRYPOINT ["java","-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=18011","-jar","/app.jar"]
#ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=18000","-jar","/app.jar"]
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]