FROM repository-hc.openplatform.healthcare/synthea:6
ARG env
ENV profile=${env}
RUN echo "oh $profile"
VOLUME /tmp
RUN mkdir -p synthea/output/fhir
ADD build/libs/dhp-synthea-service-0.0.1-SNAPSHOT.jar app.jar
#debug entrypoint
#ENTRYPOINT ["java","-agentlib:jdwp=transport=dt_socket,address=8000,server=y,suspend=n","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${profile}","-jar","app.jar"]
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=${profile}","-jar","app.jar"]
