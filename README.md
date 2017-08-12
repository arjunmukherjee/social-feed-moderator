Social media content moderator (with sentiment analysis)
=================================
![About](https://github.com/arjunmukherjee/social-feed-moderator/blob/master/public/images/about.png)


#### Current
  - Twitter integration : will pull down the tweets from twitter relevant to the query string, will attempt to analyse the text to figure out the sentiment.
#### Future
  - Facebook
  - Instagram
  - User defined

### Compile
`sbt compile`

### Environment/config variables
Ensure you have the following environment/config variables set. You can set this in your shell, or in `conf/application.conf`
  ```
  twitter.consumerKey = "YOUR KEY"
  twitter.consumerSecret = "YOUR CONSUMER SECRET"
  twitter.accessToken = "YOUR TOKEN"
  twitter.accessTokenSecret = "YOUR TOKEN SECRET"
  twitter.queryString = "PulpFiction OR ReservoirDogs"
  ```
  
### Run
- `sbt run`
- check `http://localhost:9000` on your favorite browser

![Main Page](https://github.com/arjunmukherjee/social-feed-moderator/blob/master/public/images/mainpage.png)
![Tweet Page](https://github.com/arjunmukherjee/social-feed-moderator/blob/master/public/images/tweetpage.png)

