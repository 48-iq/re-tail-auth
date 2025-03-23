FROM eclipse-temurin:21-jdk-jammy AS build
WORKDIR /app
COPY .mvn .mvn
COPY mvnw .
COPY ./pom.xml ./pom.xml
RUN ./mvnw dependency:resolve
COPY ./src ./src
RUN ./mvnw package -DskipTests

FROM eclipse-temurin:21-jre-jammy
COPY --from=build /app/target/*.jar ./app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]