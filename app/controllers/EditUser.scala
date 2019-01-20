package controllers

import models.UserInformation
import play.api.mvc._
import play.api.Play.current
import play.api.i18n.Messages.Implicits._

object EditUser extends Controller{

  def showUserProperties() = Action { implicit request =>

    val currentUser = request.session.get("emailAddress")

    Ok(views.html.user(currentUser)(UserInformation.getAllInformationsForUser(currentUser), UserInformation.form))

  }

  def create() = Action { implicit request =>

    val currentUser = request.session.get("emailAddress")

    UserInformation.form.bindFromRequest.fold(
      errors => BadRequest(views.html.user(currentUser)(UserInformation.getAllInformationsForUser(currentUser), errors)),
      userInformation => {
        UserInformation.createInformation(userInformation, currentUser)
        Redirect(routes.EditUser.showUserProperties())
      }
    )
  }

  def edit(id: Long) = Action { implicit request =>

    val currentUser = request.session.get("emailAddress")

    UserInformation.getInformation(id).map { userInformation =>
      Ok(views.html.edit(currentUser)(id, UserInformation.form.fill(userInformation)))
    } getOrElse {
      Redirect(routes.Application.index())
    }
  }

  def update(id: Long) = Action { implicit request =>

    val currentUser = request.session.get("emailAddress")

    UserInformation.form.bindFromRequest.fold(
      errors => BadRequest(views.html.edit(currentUser)(id, errors)),
      contact => {
        UserInformation.updateInformation(id, contact)
        Redirect(routes.EditUser.showUserProperties())
      }
    )
  }

  def delete(id: Long) = Action {
    UserInformation.delete(id)
    Redirect(routes.EditUser.showUserProperties())
  }

}