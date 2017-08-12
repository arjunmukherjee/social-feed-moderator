package controllers

import com.google.inject.Inject
import com.social.twitter.TwitterIntegration
import org.joda.time.format.DateTimeFormat
import play.api.Logger
import play.api.mvc.{Action, Controller}

class TwitterController @Inject() (twitterIntegration: TwitterIntegration)extends Controller {

  lazy private val errorMessage = "Invalid Date Format, please use YYYY-MM-DD"
  lazy private val log = Logger(getClass)
  private val rejectedMap: scala.collection.mutable.Map[String, scala.collection.mutable.Set[String]] = scala.collection.mutable.Map[String, scala.collection.mutable.Set[String]]()
  private val approvedMap: scala.collection.mutable.Map[String, scala.collection.mutable.Set[String]] = scala.collection.mutable.Map[String, scala.collection.mutable.Set[String]]()

  def getDailyTweets(tweetDate: String) = Action { request =>
    if (validateDate(tweetDate)){
      val tweets = twitterIntegration.getTweetsByDate(tweetDate)
      Ok(views.html.tweetdisplay(tweets,tweetDate,
                                 approvedMap.get(tweetDate),rejectedMap.get(tweetDate), tweets.length - getNumberOfActionedTweets(tweetDate)))
    } else{
      BadRequest(views.html.errorpage(errorMessage))
    }
  }

  def approveTweet(tweetId: Long, tweetDate: String) = Action { request =>
    if (approvedMap.get(tweetDate).isDefined ){
      approvedMap(tweetDate) += tweetId.toString
    } else {
      approvedMap.put(tweetDate,scala.collection.mutable.Set(tweetId.toString))
    }
    val tweets = twitterIntegration.getTweetsByDate(tweetDate)
    Ok(views.html.tweetdisplay(tweets,tweetDate,
      approvedMap.get(tweetDate),rejectedMap.get(tweetDate),tweets.length - getNumberOfActionedTweets(tweetDate)))
  }

  def rejectTweet(tweetId: Long, tweetDate: String) = Action { request =>
    if (rejectedMap.get(tweetDate).isDefined ){
      rejectedMap(tweetDate) += tweetId.toString
    } else {
      rejectedMap.put(tweetDate,scala.collection.mutable.Set(tweetId.toString))
    }
    val tweets = twitterIntegration.getTweetsByDate(tweetDate)
    Ok(views.html.tweetdisplay(tweets,tweetDate,
      approvedMap.get(tweetDate),rejectedMap.get(tweetDate),tweets.length - getNumberOfActionedTweets(tweetDate)))
  }

  def getApprovedTweets(tweetDate: String) = Action { request =>
    if (validateDate(tweetDate)) {
      Ok(views.html.actionedtweets("Approved", approvedMap.get(tweetDate), tweetDate))
    } else{
      BadRequest(views.html.errorpage(errorMessage))
    }
  }

  def getRejectedTweets(tweetDate: String) = Action { request =>
    if (validateDate(tweetDate)) {
      Ok(views.html.actionedtweets("Rejected", rejectedMap.get(tweetDate), tweetDate))
    } else{
      BadRequest(views.html.errorpage(errorMessage))
    }
  }

  private def validateDate(tweetDate : String) : Boolean = {
    try {
      val dateTimeFormat = "yyyy-MM-dd"
      val tweetDateFormatter = DateTimeFormat.forPattern(dateTimeFormat)
      tweetDateFormatter.parseDateTime(tweetDate)
    } catch {
      case e :Exception =>
        log.warn(s"Invalid date argument $tweetDate")
        return false
    }
    true
  }

  private def getNumberOfActionedTweets(tweetDate: String) = {
    var totalActionedTweets = 0
    if (approvedMap.get(tweetDate).isDefined ){
      totalActionedTweets = approvedMap(tweetDate).size
    }
    if (rejectedMap.get(tweetDate).isDefined ){
      totalActionedTweets += rejectedMap(tweetDate).size
    }
    totalActionedTweets
  }
}
