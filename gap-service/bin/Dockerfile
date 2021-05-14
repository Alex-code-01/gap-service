FROM openjdk:8
MAINTAINER antonio.dm.chacon@gmail.com
COPY . /opt/app
WORKDIR /opt/app
RUN chmod +x mvnw
RUN ./mvnw -v
RUN ./mvnw clean install -U -DskipTests
RUN mkdir -p /opt/gap-service
RUN mv /opt/app/target/gap-service-*.jar /opt/gap-service/gap-service.jar
RUN rm -r /opt/app /root/.m2
ENTRYPOINT ["java"]
CMD ["-jar", "/opt/gap-service/gap-service.jar"]
EXPOSE 8080