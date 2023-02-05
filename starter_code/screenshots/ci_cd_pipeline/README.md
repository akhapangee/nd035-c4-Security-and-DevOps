## Overview
* We will create an EC2 Instance on AWS.
* We will install docker.
* We will create two containers for jenkins and tomcat server.
* We will be using Docker image from DockerHub https://hub.docker.com/r/jenkinsci/blueocean/ for jenkins without creating docker file.
* We will be using pre-created docker image https://hub.docker.com/_/tomcat for tomcat server without creating docker file.

1. Create EC2 Instance on AWS
   * Lunch Instance on EC2 Dashboard
   * Create Security Group with inbound and outbound connection defining ports.
   * Create Security Key and download to you local folder.
   
2. Connect to SSH with SSH client
    ```
    #Lunch EC2 Instance
    chmod 400 AWS_EC2_DemoKey.pem
    
    # Connect to EC2 Instance
    ssh -i "AWS_EC2_DemoKey.pem" ec2-user@ec2-54-146-91-112.compute-1.amazonaws.com
    ```
3. Install docker
    ```
    # update the existing packages
    sudo yum update
    # download and install Docker
    sudo yum install docker
    
    # Add the $USER user to the "docker" user group
    # The current $USER is ec2-user
    sudo usermod -a -G docker $USER
    sudo reboot
    ```
   
4. Reconnect to instance and create a container 'myContainer' for jenkins server
    ```
    #start Docker service
    sudo service docker start
    
    # Check if the Docker engine is running
    systemctl show --property ActiveState docker
    
    # Create and run a new Container using the "jenkinsci/blueocean" image
    docker run -u root -d --name myContainer -p 8080:8080 -v jenkins-data:/var/jenkins_home -v /var/run/docker.sock:/var/run/docker.sock -v "$HOME":/home jenkinsci/blueocean

    # Install Maven dependency
    docker exec -it myContainer bash
    apk add maven 
   ```
   *  Verify the jenkins page is now accessible at http://<public ip add>:8080
   *  Set up admin password as per following:
    ```
    # Open the bash into the container
    docker exec -it myContainer bash
    
    # View the file
    cat /var/jenkins_home/secrets/initialAdminPassword
    ```
5. Generate private and public key from 'myContainer'.
   * Add private key to Jenkins global credentials
   * Add public key to Github repository
    ```
    # Generate keys
    ssh-keygen -t rsa
    
    # View the private key
    cat /root/.ssh/id_rsa
    
    # View the pubic key
    cat /root/.ssh/id_rsa.pub
    ```
   
6. Install tomcat on same instance
    ```
    # install tomcat
    docker run -dit --name myTomcatServer -p 8888:8080 tomcat:jdk8
    docker exec -it myTomcatServer bash
    
    # View the files inside the /usr/local/tomcat folder. Notice the webapps folder
    ls -l
    # Copy the files from webapps.dist to the webapps folder
    cp -r webapps.dist/* webapps
    
    # Come out of the bash, when needed
    exit
    ```
    * View tomcat installed on http://<ip address>:8888/
    * Add user:
    ```
    # Install VIM editor because we need to edit a few files
    apt-get update
    apt-get install vim
    
    # Open the file with an editor
    vi conf/tomcat-users.xml
    
    Add below line to xml file
    <role rolename="admin-gui"/>
    <role rolename="manager-gui"/>
    <role rolename="manager-script"/>
    <user username="admin" password="admin" roles="admin-gui, manager-gui, manager-script"/>
    
    # Find the context.xml files
    find . -name context.xml
    # Open the manager context file in VIM editor
    vi ./webapps/manager/META-INF/context.xml
    
    @@Comment following
    <Valve className="org.apache.catalina.valves.RemoteAddrValve"
    allow="127\.\d+\.\d+\.\d+|::1|0:0:0:0:0:0:0:1" />
    
    # Shutdown the server
    ./bin/shutdown.sh
    docker start myTomcatServer
    ```
    * Login to http://<ip address>:8888/manager/html

7. Jenkins Setup
   * Install suggested plugins including 'Deployment to Container' and 'Maven Integration' plugins
   * Set up 'Manage Credentials'
   * Set up 'Global configurations.'
   * Set up project.

8. Some useful commands:
    ```
    # start/stop container
    docker start/stop <container_id> or name
   
   # Remove
    docker container rm <container_ID>
    docker container rm 8aaa6f997d12
    ```