FROM eclipse-temurin:17-jre

WORKDIR /app

# Copy the built jar from the target directory directly
# This is the most reliable way as it doesn't depend on Nexus paths during build
COPY target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java","-jar","app.jar"]
