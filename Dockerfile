FROM maven:3.6.1-jdk-8 as maven_builder
WORKDIR /book-rest
ADD pom.xml .
ADD src ./src
ADD tomcat/tomcat-users.xml .
RUN mvn clean package
FROM tomcat:8.5.43-jdk8
COPY --from=maven_builder /book-rest/target/book.war /usr/local/tomcat/webapps
COPY --from=maven_builder /book-rest/tomcat-users.xml /usr/local/tomcat/conf
