mvn -B -DskipTests clean package
sudo docker build -t tomcat_book:latest .
