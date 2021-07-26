sudo docker build -t tomcat-book:latest .
sudo docker build -t mysql:latest -f Dockerfile_mysql
sudo docker network create book-net
sudo docker run -d --name mysql --network book-net --network-alias mysql.mysqlns mysql:latest
sudo docker run -d --name tomcat --network book-net --network-alias tomcat tomcat-book:latest
mvn verify
sudo docker stop mysql tomcat
sudo docker system prune -f