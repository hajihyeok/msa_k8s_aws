# # jdk image pull 
# FROM eclipse-temurin:17-jdk-alpine

# # jar
# ARG JAR_FILE=build/libs/*.jar

# # copy
# COPY ${JAR_FILE} ./backend.jar

# # run 
# ENTRYPOINT ["java", "-jar", "./backend.jar"]

# build stage (JDK + gradle) -> jar
FROM eclipse-temurin:17-jdk-alpine as builder
WORKDIR /app

COPY gradlew . 
COPY gradle gradle
COPY build.gradle settings.gradle ./

RUN  chmod +x gradlew
RUN  ./gradlew dependencies 

COPY src src
RUN  ./gradlew bootJar

# runtime stage (jre) -> jar
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]