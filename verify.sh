sudo docker build -t tomcat-book:latest .
sudo docker build -t mysql:latest -f Dockerfile_mysql .
sudo docker network create book-net
sudo docker run -d --name mysql --network book-net --network-alias mysql.mysqlns mysql:latest
sudo docker run -d --name tomcat --network book-net --network-alias tomcat -e JAVA_OPTS='-DDB_USER=domcretan -DDB_PWD=admin_root_kyr1' tomcat-book:latest
sudo docker run -it --rm --name my-maven-project -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven --network book-net maven:3.3-jdk-8 mvn verify
sudo docker stop mysql tomcat
sudo docker system prune -f