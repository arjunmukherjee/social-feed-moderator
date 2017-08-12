package controllers

import play.api.mvc.{Action, Controller}

class HealthController extends Controller {
  def health = Action { request =>
    Ok("This application is basically functional")
  }
}
