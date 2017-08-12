package controllers

import play.api.mvc.{Action, Controller}


class HomeController extends Controller {
  def index = Action {
    Ok(views.html.index())
  }
}
