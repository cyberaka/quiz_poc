# Build context: codebase/quiz-parent (the multi-module Maven root).
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /src
COPY pom.xml .
COPY quiz-common/pom.xml quiz-common/
COPY quiz-domain/pom.xml quiz-domain/
COPY quiz-dao/pom.xml quiz-dao/
COPY quiz-service/pom.xml quiz-service/
COPY quiz-ws/pom.xml quiz-ws/
RUN mvn -B -q dependency:go-offline -pl quiz-ws -am || true
COPY . .
RUN mvn -B package -pl quiz-ws -am

FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="cyberaka.com"
RUN addgroup -S quiz && adduser -S quiz -G quiz
COPY --from=build /src/quiz-ws/target/quiz-ws-1.0-SNAPSHOT.jar /app/quiz-ws.jar
USER quiz
EXPOSE 7070
ENTRYPOINT ["java", "-jar", "/app/quiz-ws.jar"]
