package controllers

import models.Task
import play.api.data.Forms._
import play.api.data._
import play.api.mvc._
import views._

/**
 *
 * @author Mle
 */
object MyTasks extends Controller {
  val taskForm = Form(single("tasktext" -> nonEmptyText))

  def home = Action {
    Ok(html.taskindex(Task.all(), taskForm))
  }

  def newTask = Action {
    implicit request =>
      taskForm.bindFromRequest.fold(
        errors => BadRequest(html.taskindex(Task.all(), errors)),
        success => success match {
          case label =>
            Task create label
            Ok(html.taskindex(Task.all(), taskForm))
        })
  }

  def delete(id: Long) = Action {
    Task delete id
    Ok(html.taskindex(Task.all(), taskForm))
  }
}
