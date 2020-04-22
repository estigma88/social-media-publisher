# Social Media Publisher

Publisher of social media posts using AWS Free Tier with Lambdas, DynamoDB and scheduled CloudWatch events.

## The Problem

Authors of blogs, books or ideas usually like to share their content on social media like Twitter, Linkedin, Facebook and so on, to create engagement and followers. 

However, this process is tedious, as you need to go to each social media account and publish your content, so, imagine you want to publish everyday, three times through the day, in your 4 social media accounts, that means, you will 
need to connect 4 x 3 = 12 times at day to the social media accounts. This is time you loose you can invest in creating your content.

Applications in the market exists to automate this process, where you schedule posts to different social media, to be published in a future time like Crowdfire. These applications have limitations, like amount of social media accounts, amount of scheduled posts and few analitics regarding your content.

If you want more features, you will need to pay for it.

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

### Social Media Support

The following are the social media supports:

* Linkedin
* Twitter

### Data Model

AWS Dynamodb is a documental database. The following are the documents we need:

#### OAuth1Credentials

Saves the OAuth1 credentials. This is used by Twitter API.

```json
{
  "accessToken": "xxx",
  "consumerKey": "xxx",
  "consumerSecret": "xxx",
  "id": "twitter",
  "tokenSecret": "xxx"
}
```

#### OAuth12Credentials

Saves the OAuth2 credentials. This is used by Linkedin API.

```json
{
  "accessToken": "xxx",
  "expirationDate": "2020-05-28T00:00:00",
  "id": "linkedin"
}
```

#### Posts

Saves the Posts you want to publish.

```json
{
  "description": "Hi everyone, do you really understand what Dependency Inversion is? you should, and you should use it. In this post, I talk about it.",
  "id": "1",
  "name": "Dependency Inversion: why you should NOT avoid it",
  "publishedDate": "2020-01-18T00:00:00",
  "tags": [
    "coderstower",
    "java",
    "solid",
    "oop",
    "dependencyinversion"
  ],
  "url": "https://coderstower.com/2019/03/26/dependency-inversion-why-you-shouldnt-avoid-it/"
}
```

### Endpoints

The following are the endpoints used in the application:

* /posts/next: Search in the database the oldest published post, and publish it. This endpoint is not reachable through AWS API Gateway, only through AWS Cloudwatch events.
* /oauth2/{socialAccount}/credentials: Some social accounts uses OAuth2 protocol to secure its share endpoints. This protocol generates a bareer token who expires after a defined time. This endpoint helps you to update the token if the token expired. This endpoint is reachable through AWS API Gateway.


## Releases

You can find the current releases [here](https://github.com/estigma88/social-media-publisher/releases)

## Installation

Use the package manager [pip](https://pip.pypa.io/en/stable/) to install foobar.

```bash
pip install foobar
```

## Usage

```python
import foobar

foobar.pluralize('word') # returns 'words'
foobar.pluralize('goose') # returns 'geese'
foobar.singularize('phenomena') # returns 'phenomenon'
```

## Contributing
Pull requests are welcome. For major changes, please open an issue first to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License
[MIT](https://choosealicense.com/licenses/mit/)