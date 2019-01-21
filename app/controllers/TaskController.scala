package controllers

import models.TaskForm
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

object TaskController extends Controller{

  def showUserProperties() = Action { implicit request =>

    val currentEmail = request.session.get("emailAddress")

    val login = TaskForm.getLoginByEmail(currentEmail.get)

    Ok(views.html.user(currentEmail)(login)(TaskForm.getAllInformationsForUser(currentEmail), TaskForm.form))

  }

  def create() = Action { implicit request =>

    val currentEmail = request.session.get("emailAddress")

    val login = TaskForm.getLoginByEmail(currentEmail.get)

    TaskForm.form.bindFromRequest.fold(
      errors => BadRequest(views.html.user(currentEmail)(login)(TaskForm.getAllInformationsForUser(currentEmail), errors)),
      task => {
        TaskForm.createTask(task, currentEmail)
        Redirect(routes.TaskController.showUserProperties())
      }
    )
  }

  def edit(id: Long) = Action { implicit request =>

    val currentUser = request.session.get("emailAddress")

    TaskForm.getTask(id).map { taskForm: TaskForm =>
      Ok(views.html.edit(currentUser)(id, TaskForm.form.fill(taskForm)))
    } getOrElse {
      Redirect(routes.MainController.index())
    }
  }

  def update(id: Long) = Action { implicit request =>

    val currentUser = request.session.get("emailAddress")

    TaskForm.form.bindFromRequest.fold(
      errors => BadRequest(views.html.edit(currentUser)(id, errors)),
      contact => {
        TaskForm.updateInformation(id, contact)
        Redirect(routes.TaskController.showUserProperties())
      }
    )
  }

  def delete(id: Long) = Action {
    TaskForm.delete(id)
    Redirect(routes.TaskController.showUserProperties())
  }

}
