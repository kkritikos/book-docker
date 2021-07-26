FROM tomcat:8.5.43-jdk8
ADD ./book-rest/target/book.war /usr/local/tomcat/webapps
COPY ./tomcat-users.xml /usr/local/tomcat/conf
RUN sed -i 's/port="8080"/port="30000"/' /usr/local/tomcat/conf/server.xml
