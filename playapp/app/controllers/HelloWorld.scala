package controllers

import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import util.PlayLog
import views._


object HelloWorld extends Controller with PlayLog {

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
    Ok(html.helloIndex(helloForm))
  }

  /**
   * Handles the form submission.
   */
  def sayHello = Action {
    implicit request =>
      helloForm.bindFromRequest.fold(
        formErrors => BadRequest(html.helloIndex(formErrors)),
        formSuccess => formSuccess match {
          case (name, repeat, color) => Ok(html.hello(name, repeat, color))
        }
      )
  }

  def printId(id: Int) = Action {
    Ok("Well done, id: " + id)
  }

  def money = TODO

  def secret = Action {
    Redirect(routes.HelloWorld.printId(666))
  }
}
