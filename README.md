# Dominion Kingdom

Application to help pick kingdoms for the board game Dominion.

# AWS Lambda

Build the code and deploy with AWS SAM.

mvn clean package

aws cloudformation package --template-file sam.yaml --output-template-file target/output-sam.yaml --s3-bucket dominion-kingdom
 
aws cloudformation deploy --template-file target/output-sam.yaml --stack-name dominion-kingdom --capabilities CAPABILITY_IAM
 
aws cloudformation describe-stacks --stack-name dominion-kingdom


# Run

To build and run from a packaged jar locally:

    mvn spring-boot:run

or 

    mvn clean package -Dboot
    java -jar target/dominion-kingdom-1.0.0-SNAPSHOT.jar

# Docker

To build the image. First build the application, then build the docker image

    mvn package -Dboot
    docker build -t dominion-kingdom .
    
## Run

    docker run --name dominion-kingdom -p 8080:8080 -d dominion-kingdom
    
# Test

    curl http://localhost:8080/box?box=Dominion
