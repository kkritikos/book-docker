export DB_USER=domcretan
export DB_PWD=admin_root_kyr1
sudo docker run -it --rm --name my-maven-project -v "$(pwd)":/usr/src/mymaven -w /usr/src/mymaven --network book-net maven:3.8.1-adoptopenjdk-11 mvn verify