FROM eclipse-temurin:17-jre

ARG NEXUS_URL=http://nexus:8081
ARG GROUP_PATH=com/example/twitter-app
ARG VERSION=0.0.8-SNAPSHOT

WORKDIR /app

RUN apt-get update && apt-get install -y curl

RUN curl -f \
  ${NEXUS_URL}/repository/maven-snapshots/${GROUP_PATH}/${VERSION}/twitter-app-${VERSION}.jar \
  -o app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
