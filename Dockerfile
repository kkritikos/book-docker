FROM tomcat:8.5.43-jdk8
COPY --from=maven_builder book-rest/target/book.war /usr/local/tomcat/webapps
RUN sed -i 's/port="8080"/port="30000"/' /usr/local/tomcat/conf/server.xml
