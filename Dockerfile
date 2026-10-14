# Build stage: compile the ATG Miner sources against the bundled dependency jar
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /build
COPY pom.xml .
COPY repo ./repo
RUN mvn -B -q dependency:go-offline
COPY src ./src
RUN mvn -B -q package

# Run stage: execute the evaluation on the examples mounted at /work/examples
FROM eclipse-temurin:11.0.24_8-jre
WORKDIR /work
COPY --from=build /build/target/atg-miner.jar /opt/atg-miner/atg-miner.jar
COPY examples ./examples
ENTRYPOINT ["java", "-Dfile.encoding=UTF-8", "-jar", "/opt/atg-miner/atg-miner.jar"]
CMD ["examples/"]
