sudo docker network create book-net
sudo docker run -d --name mysql --network book-net --network-alias mysql -v mysql:/var/lib/mysql -v `pwd`/mysql:/docker-entrypoint-initdb.d -e MYSQL_ROOT_PASSWORD=$MYSQL_ROOT_PASSWORD mysql
sudo docker run -d --name tomcat --network book-net -p 80:30000 -e JAVA_OPTS="-DDB_USER=$DB_USER -DDB_PWD=$DB_PWD" tomcat_book:latest