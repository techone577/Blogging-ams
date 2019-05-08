FROM carsharing/alpine-oraclejdk8-bash
VOLUME /tmp
ADD ams-0.0.1-SNAPSHOT.jar ams.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/ams.jar"]