# Social Media Publisher  
  
Publisher of social media posts using AWS Free Tier with Lambdas, DynamoDB and scheduled CloudWatch events.  
  
## The Problem  
  
Authors of blogs, books or ideas usually like to share their content on social media like Twitter, Linkedin, Facebook and so on, to create engagement and followers.   
  
However, this process is tedious, as you need to go to each social media account and publish your content, so, imagine you want to publish everyday, three times through the day, in your 4 social media accounts, that means, you will   
need to connect 4 x 3 = 12 times at day to the social media accounts. This is time you loose you can invest in creating your content.  
  
Applications in the market exists to automate this process, where you schedule posts to different social media, to be published in a future time like Crowdfire. These applications have limitations, like amount of social media accounts, amount of scheduled posts and few analytics regarding your content.  
  
If you want more features, you will need to pay for it.

I decided to build my own. For more details about why and how, you can check my post [How I Built a Social Media Publisher on AWS for Free](https://coderstower.com/2020/09/28/how-i-built-a-social-media-publisher-on-aws-for-free/). 
  
## The Solution  
  
A lambda function who reads posts saved somewhere and publish them in a defined time and rate.  
  
Technologies:  
* Spring Boot  
* Spring Security  
* Spring MVC  
* Spring Repositories  
* AWS Lambda  
* AWS Dynamodb  
* AWS API Gateway  
* AWS Cloudwatch  
  
The AWS technologies stay in the forever free services, which means, you won't be charged for deploying this application to production.  
  
## Installation  
You need to create a AWS infrastructure to deploy the app as follows.
  
### Social Media Support  
  
The following are the social media supports:  
  
* Linkedin: You need to create a client app (OAuth2) [here](https://www.linkedin.com/developers/).
* Twitter : You need to create a client app (OAuth1.1) [here](https://developer.twitter.com/en/apps).

### AWS Dynamodb
  
AWS Dynamodb is a documental database. You should create the following tables:  
  
#### OAuth1Credentials  
  
Saves the OAuth1 credentials. This is used by Twitter API.  

You need to create a record with id "twitter" and the below information can be obteined from the Twitter app you created. Th OAuth1 credentials are static, they won't expire. The following is an example:
  
```json  
{  
  "accessToken": "xxx",  
  "consumerKey": "xxx",  
  "consumerSecret": "xxx",  
  "id": "twitter",  
  "tokenSecret": "xxx"  
}  
```  
  
#### OAuth2Credentials  
  
Saves the OAuth2 credentials. This is used by Linkedin API.  

You need to create a record with id "linkedin" and the below information will be updated using the Solcial Media Publisher app after you deploy it to production. You will see how in the following sections.

The OAuth2 credentials expire, so, you will need to update them before it expires. The Social Media Publisher app will help you. The following is an example:
  
```json  
{  
  "accessToken": "xxx",  
  "expirationDate": "2020-05-28T00:00:00",  
  "id": "linkedin"  
}  
```  
  
#### Posts  
  
Saves the Posts you want to publish.  The following is an example:
  
```json  
{  
  "description": "Hi everyone, do you really understand what Dependency Inversion is? you should, and you should use it. In this post, I talk about it.",  
  "id": "1",  
  "name": "Dependency Inversion: why you should NOT avoid it",  
  "publishedDate": "2020-01-18T00:00:00",  
  "tags": [  
 "coderstower", "java", "solid", "oop", "dependencyinversion" ],  "url": "https://coderstower.com/2019/03/26/dependency-inversion-why-you-shouldnt-avoid-it/"  
}  
```  
### AWS Lambda
You should grab the latest release of the Social Media Publisher app and deployed to a Java lambda function.
You will need to add the following variables as environment variables:

 - `spring.profiles.active`: You can activate `twitter` or `linkedin`, or both.

You will need to build a JSON with those environment variables and set the AWS Lambda variable to SPRING_APPLICATION_JSON. A JSON example is shown below:

```json  
{ 
  "spring": { 
              "profiles": { 
                            "active": "linkedin,twitter" 
                          }
             }
}
```  

Also, the handler method for the Lambda should be `com.coderstower.socialmediapubisher.springpublisher.main.aws.StreamLambdaHandler::handleRequest`

Besides, as destination, you should connect the Lambda with AWS SNS, so, if the Lambda execution suceed or fail, you will get an email.

### AWS Cloudwatch
You will use Cloudwatch to schedule when you want to publish the posts to the registered social networks. So, you need to create a event, with a cron expresion configuration, for instance `0 18 ? * SUN,WED,FRI *` which means the posts will be publish each Sunday, Wednesday and Friday, at 18 hours UTC.

As target, you will add the AWS Lambda you created before, sending a fake gateway event to simulate calling the endping `/posts/next`. You can find a fake gateway event in the AWS Lambda console, when you create tests requests, you can create a Gateway one. The JSON generated could be used from AWS Cloudwatch.

## Updating OAuth2 Credentials
OAuth2 credentials expire, so, we need to refresh them frequently.

This process is handle by the Social Media Publisher application, but, not deployed in AWS, instead, running locally in your machine. The following are the steps:

1. Login to AWS using the `aws-cli configure` operation.
2. Setup the necessary properties in the application-secure.yml file

    - `social-media-publisher.principal-names-allowed.linkedin`: This is your unique Linkedin id.
    - `spring.security.oauth2.client.registration.linkedin.client-id`: This is the Linkedin cliend id. You can find the value in the Linkedin app you created before.
    - `spring.security.oauth2.client.registration.linkedin.client-secret`: This is the Linkedin cliend secret. You can find the value in the Linkedin app you created before.

3. Start the application using the AWSSpringPublisherApplication class and the secure spring profile active:
    - `spring.profiles.active`: You must activate `secure` profile.

4. Access the url `http://localhost:8080/oauth2/linkedin/credentials`. The application will redirect to login on LinkedIn, and after you set your credentials, the OAuth2 credentials for linkedin will be updated in DynamoDB
  
## Local Testing
You can run the application locally and connect it to a local DynamoDB as follows:

1. Run AWS Dynamodb locally: `docker run -p 8000:8000 amazon/dynamodb-local -jar DynamoDBLocal.jar -inMemory -sharedDb`
2. Create the tables locally:

    - `aws dynamodb create-table --table-name OAuth1Credentials --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --endpoint-url http://localhost:8000`
    - `aws dynamodb create-table --table-name OAuth2Credentials --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --endpoint-url http://localhost:8000`
    - `aws dynamodb create-table --table-name Posts --attribute-definitions AttributeName=id,AttributeType=S --key-schema AttributeName=id,KeyType=HASH --provisioned-throughput ReadCapacityUnits=5,WriteCapacityUnits=5 --endpoint-url http://localhost:8000`

3. Check the tables were created: `aws dynamodb list-tables --endpoint-url http://localhost:8000`
4. Start the application using the AWSSpringPublisherApplication class and the secure spring profile active:
    - `spring.profiles.active`: You must activate `local` profile. If you want to do a full test with your social media accounts, add `local,linkedin,twitter`

## Releases  
  
You can find the current releases [here](https://github.com/estigma88/social-media-publisher/releases)  
  
## Contributing  
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.  
  
Please make sure to update tests as appropriate.  
  
## License  
[MIT](https://choosealicense.com/licenses/mit/)