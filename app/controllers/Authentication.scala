package controllers

import models.LoginUser
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

object Authentication extends Controller{

  def login() = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    Ok(views.html.login(LoginUser.loginForm)(userEmailAddress))
  }

  /**
    * Logout and clean the session.
    */
  def logout() = Action {
    Redirect(routes.Authentication.login).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  /**
    * Handle login form submission.
    */
  def authenticate = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    LoginUser.loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.login(formWithErrors)(userEmailAddress)),
      user => Redirect(routes.Application.index()).withSession("emailAddress" -> user.emailAddress)
    )
  }

}
