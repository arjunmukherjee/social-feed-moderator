package com.social.twitter

import com.google.inject.Inject
import com.social.sentiment.TweetSentimentAnalyzer
import org.joda.time.DateTime
import play.api.{Configuration, Logger}
import twitter4j.{MediaEntity, Query, TwitterFactory, URLEntity}
import twitter4j.conf.ConfigurationBuilder

import scala.collection.JavaConverters._

case class Tweet(id: Long, text: String, userName: String, userURL: String, sentiment: String, urlList: List[URLEntity], attachmentList: List[MediaEntity])

class TwitterIntegration @Inject()(configuration: Configuration) {

  val INVALID = "invalid"
  val logger = Logger(getClass)

  def getTweetsByDate(tweetDate: String) : List[Tweet] = {
    val tweetEndDate = DateTime.parse(tweetDate).plusDays(1).toString("YYYY-MM-dd")
    val cb = new ConfigurationBuilder()
    cb.setDebugEnabled(true)
      .setOAuthConsumerKey(configuration.getString("twitter.consumerKey").getOrElse(INVALID))
      .setOAuthConsumerSecret(configuration.getString("twitter.consumerSecret").getOrElse(INVALID))
      .setOAuthAccessToken(configuration.getString("twitter.accessToken").getOrElse(INVALID))
      .setOAuthAccessTokenSecret(configuration.getString("twitter.accessTokenSecret").getOrElse(INVALID))
    val tf = new TwitterFactory(cb.build())
    val twitter = tf.getInstance()

    val queryString = configuration.getString("twitter.queryString").getOrElse("PulpFiction OR ReservoirDogs")

    logger.info(s"Looking for [$queryString] from [$tweetDate] to [$tweetEndDate]")

    val query = new Query(queryString)
    query.setSince(tweetDate)
    query.setUntil(tweetEndDate)
    val tweets = twitter.search(query).getTweets.asScala.toList
      .filterNot(tweet => tweet.isRetweet)
      .filter(tweet => tweet.getLang == "en")

    tweets.map { tweet =>
      Tweet(tweet.getId, stripUrlFromTweet(tweet.getText),tweet.getUser.getScreenName,tweet.getUser.getURL,
            TweetSentimentAnalyzer.findSentiment(stripUrlFromTweet(tweet.getText)),
            tweet.getURLEntities.toList,tweet.getMediaEntities.toList)
    }
  }

  private def stripUrlFromTweet(tweet: String): String = {
    val indexOfHttp = tweet.lastIndexOf("http")
    if (indexOfHttp != -1 ) {
      val slicedTweet = tweet.substring(0, indexOfHttp)
      slicedTweet
    } else {
      tweet
    }
  }
}
