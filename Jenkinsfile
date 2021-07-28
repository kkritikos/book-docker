node {
    checkout scm
    
    def mvnImage = docker.image('maven:3.8.1-adoptopenjdk-11')
    def postfix
    def label = 'kkritikos/book:'
    if (env.BRANCH_NAME=='eks'){
         label += 'latest-stable'
         postfix='prod'
    }
    else if (env.BRANCH_NAME=='ecs'){
         label += 'latest'
         postfix='prod'
    }
    else{
         label += ('dev-' + env.BUILD_ID)
         postfix='dev' + env.BUILD_ID
    }
    	 
    stage('Init_Clean'){
    	   mvnImage.inside(){
		     sh 'echo "Build is starting!!!"'    	       
    	   }
    	   try{
    	    	sh 'docker container stop mysql_' + postfix + ' tomcat_' + postfix   
    	   }
    	   catch(e){}
    	   sh 'docker system prune -f'
    	   
    }
    
    def sqlImage
    def tomcatImage
    stage('Build') {
            mvnImage.inside(){
                sh 'mvn -B -DskipTests clean package'
            }
            sqlImage = docker.build("mysql:latest", "-f Dockerfile_mysql .")
            tomcatImage = docker.build(label, "-f Dockerfile .")
    }
    
    stage('Test'){
    	//environment {
	        //DB_USER = credentials('DB_USER')
        	//DB_PWD = credentials('DB_PWD')
	    //}
        try{
        	sh 'docker network create book-net_' + postfix
        	sh 'docker run -d --name mysql_' + postfix + ' --network book-net_' + postfix + ' --network-alias mysql.mysqlns mysql:latest'
        	//sh("docker run -d --name tomcat_" + postfix + " --network book-net_" + postfix + " --network-alias tomcat -e JAVA_OPTS=-DDB_USER=$DB_USER -e CATALINA_OPTS=-DDB_PWD=$DB_PWD " + label)
        	sh 'docker run -d --name tomcat_' + postfix + ' --network book-net_' + postfix + ' --network-alias tomcat -e JAVA_OPTS=-DDB_USER=domcretan -e CATALINA_OPTS=-DDB_PWD=admin_root_kyr1 ' + label
        	mvnImage.inside('--network book-net_' + postfix){
      			sh 'mvn verify'   
        	}    
        }
        catch(e){
        	echo 'Something went wrong during testing'
        	throw e    
        }
		finally{
		    try{
				sh 'docker container stop mysql_' + postfix + ' tomcat_' + postfix
				sh 'docker container rm mysql_' + postfix + ' tomcat_' + postfix
				sh 'docker system prune -f'		        
		    }
		    catch(e){
		        echo 'Something went wrong while destroying the testing containers'
		    }

		    mvnImage.inside(){
                	junit 'book-functional-tests/target/failsafe-reports/*.xml'
            }
		}
    }
    
    stage('Push') {
    	docker.withRegistry('https://index.docker.io/v1/', 'Dockerhub') {
        	tomcatImage.push()
        }
    }
    
    stage('Deploy') {
    	if (env.BRANCH_NAME=='ecs'){
    		sh 'docker run --rm -v ~/.aws:/root/.aws amazon/aws-cli:latest ecs update-service --cluster myCluster2 --service book-service --deployment-configuration maximumPercent=200,minimumHealthyPercent=50 --force-new-deployment'
    	    //docker.image('amazon/aws-cli:latest').
    	    //run('--rm -it --name aws -v ~/.aws:/root/.aws', 'ecs --cluster myCluster2 --service book-service --deployment-configuration maximumPercentage=200,minimumHealthyPercentage=50 --force-new-deployment')
    	    
    	}

    }
}