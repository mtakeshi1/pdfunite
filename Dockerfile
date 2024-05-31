FROM eclipse-temurin:22.0.1_8-jdk-ubi9-minimal

EXPOSE 8080
ADD ./build/libs/pdfunite-0.0.1-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "-jar", "/app.jar"]
