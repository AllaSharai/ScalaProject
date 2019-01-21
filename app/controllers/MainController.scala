package controllers

import models.CreateUserForm
import play.api.Play.current
import play.api.i18n.Messages.Implicits._
import play.api.mvc._

object MainController extends Controller {

  def index() = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    Ok(views.html.index(userEmailAddress))
  }

  def showCreateUser() = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    Ok(views.html.createaccount(CreateUserForm.createAccountForm)(userEmailAddress))
  }

  def createUser() = Action { implicit request =>

    val userEmailAddress = request.session.get("emailAddress")

    CreateUserForm.createAccountForm.bindFromRequest.fold(
      errors => BadRequest(views.html.createaccount(errors)(userEmailAddress)),
      user => {
        CreateUserForm.createUser(user)
        Redirect(routes.MainController.index())
      }
    )
  }

  def testPage() = Action { implicit request =>
    val userEmailAddress = request.session.get("emailAddress")
    Ok(views.html.testPage(userEmailAddress))
  }

}