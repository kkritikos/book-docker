sudo docker network create book-net
sudo docker run -d --name mysql --network book-net --network-alias mysql -v mysql:/var/lib/mysql mysql_book:latest
sudo docker run -d --name tomcat --network book-net -p 8080:8080 tomcat_book:latest
