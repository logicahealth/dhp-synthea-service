FROM robcaruso/synthea:1
ARG env
ENV profile=${env}
RUN echo "oh $profile"
VOLUME /tmp
ADD build/libs/dhp-synthea-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${profile}","-jar","app.jar"]
