## Overview
* Deploy jenkins build file to a new container residing on a NEW EC2 instance

1. Launch a new host EC2 instance  
   Launch a new EC2 instance, based on Amazon Linux 2 AMI and t2.small/t2.micro. Let's name it Host_2, assuming we already have Jenkins running inside a container on Host_1.

2. Install docker on EC2 instance
   Connect to Host_2 using SSH, and install docker:
   ```
   # update the existing packages
   sudo yum update
   sudo  yum install docker
   ```

3. Create a new user for Docker management, and add that user to Docker (default) group.
   ```
   sudo useradd host2admin
   sudo passwd host2admin
   
   # Add the host2admin user to the "docker" user group
   sudo usermod -aG docker host2admin
   
   # Add the $USER user to the "docker" user group. The current $USER is ec2-user
   sudo usermod -a -G docker $USER
   sudo reboot
   ```
4. Start services
   ```
   # Reconnect using SSH. The public IP will change after reboot
   sudo service docker start
   # Verify that you can run docker commands without sudo.
   docker run hello-world
   ```

5. Write a Dockerfile under /opt/docker/ directory
   * Create the /opt/docker/ directory
   ```   
   sudo su -
   cd /opt
   mkdir docker
   cd docker
   vi Dockerfile
   ```
   * Add the following to the new Dockerfile
   ```
   # Pull base image
   From tomcat:8-jre8
   # Maintainer
   MAINTAINER "Maintainer"
   # copy war file on to container
   COPY ./*.war /usr/local/tomcat/webapps
   ```

6. Allow Jenkins' access to the Docker
   Jenkins will attempt to write files in the Docker as the newly created user "host2admin". Therefore, enable the password-based authentication
   ```
   vi /etc/ssh/sshd_config
   # Comment the passwordauthentication line
   sudo service sshd restart
   Change ownership permissions, allowing the new user "host2admin" to write here
   
   chown -R host2admin:host2admin /opt/docker/
   sudo service docker restart
   ```

7. Login to Jenkins console and add Docker server to execute commands from Jenkins
   * Manage Jenkins → Manager plugins → Install "Publish over SSH" plugin
   * Manage Jenkins → Configure system → Publish over SSH → Add the new host IP address and credentials of the newly created user

8. Create Jenkins job  
   Create a new job, mySecondJob (Type: **Maven project**), and configure with the following details (leaving remaining details as default):

   * **Source Code Management**  
   Repository : https://github.com/akhapangee/nd035-c4-Security-and-DevOps  
   Branches to build : */master  

   * **Build**  
   Root POM: pom.xml  
   Goals and options: clean install package

   * **Post Steps**  
     Add post-build steps: Choose **Send files or execute commands over SSH**  
     Name: Host_2 (Choose Verbose mode)  
     Source files: starter_code/target/*.war  
     Remove prefix: starter_code/target  
     Remote directory: //opt//docker  
     Exec command[s]:  
     ```
     docker stop demo_container;  
     docker rm -f demo_container;
     docker stop demo_image;  
     docker rm -f demo_image;
     docker image rm -f demo_image;
     cd /opt/docker;
     docker build -t demo_image .
     ```
     The commands above will remove any existing container/image with the given name, and create a fresh new image, demo_image, inside the current /opt/docker/ directory. Add another **Transfer Set**, and use the following execution command:
   
      `docker run -d --name demo_container -p 8888:8080 demo_image`

     The command above will create a new container, demo_container using the demo_image created in the previous command.

9. Execute Jenkins job
   Check images and containers again on the Host_2. A new demo_image and demo_container will get created through the Jenkins job.

