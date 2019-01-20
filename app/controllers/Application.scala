package controllers

import models.User
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

object Application extends Controller {

  def index() = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    Ok(views.html.index(userEmailAddress))
  }

  def showCreateUser() = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    Ok(views.html.createaccount(User.createAccountForm)(userEmailAddress))
  }

  def createUser() = Action { implicit request =>

    val userEmailAddress = request.session.get("emailAddress")

    User.createAccountForm.bindFromRequest.fold(
      errors => BadRequest(views.html.createaccount(errors)(userEmailAddress)),
      user => {
        User.createUser(user)
        Redirect(routes.Application.index())
      }
    )
  }

}