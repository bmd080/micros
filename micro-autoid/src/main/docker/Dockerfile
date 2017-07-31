FROM java:8
VOLUME /tmp
ADD micro-autoid-0.1.0-SNAPSHOT.jar app.jar
RUN bash -c 'touch /app.jar'
ENV RABBITMQ_HOST ${RABBITMQ_HOST_S}
ENV RABBITMQ_PORT ${RABBITMQ_PORT_S}
ENV RABBITMQ_USER ${RABBITMQ_USER_S}
ENV RABBITMQ_PASS ${RABBITMQ_PASS_S}
ENV MONGODB_HOST ${MONGODB_HOST_S}
ENV MONGODB_PORT ${MONGODB_PORT_S}
EXPOSE 9006
EXPOSE 18010
ENTRYPOINT ["java","-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=18010","-jar","/app.jar"]
#ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=18000","-jar","/app.jar"]
#ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]