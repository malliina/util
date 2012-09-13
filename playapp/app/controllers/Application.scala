package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import util.PlayLog
import views._


object Application extends Controller with PlayLog{

  /**
   * Describes the hello form.
   */
  val helloForm = Form(
    tuple(
      "name" -> nonEmptyText,
      "repeat" -> number(min = 1, max = 100),
      "color" -> optional(text)
    )
  )
  // -- Actions

  /**
   * Home page
   */
  def index = Action {
//    play.Logger.of("application") info "Logger.of says hello"
    Ok(html.index(helloForm))
  }

  /**
   * Handles the form submission.
   */
  def sayHello = Action {
    implicit request =>
      helloForm.bindFromRequest.fold(
        formErrors => BadRequest(html.index(formErrors)),
        formSuccess => formSuccess match {
          case (name, repeat, color) => Ok(html.hello(name, repeat.toInt, color))
        }
      )
  }

  def printId(id: Int) = Action {
    Ok("Well done, id: " + id)
  }

  def money = TODO

  def secret = Action {
    Redirect(routes.Application.printId(666))
  }
}
