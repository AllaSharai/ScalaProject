package models

import anorm._
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.db.DB

case class LoginUser(id: Long, emailAddress: String, password: String)

object LoginUser {

  val loginForm = Form(
    mapping(
      "id" -> ignored(0L),
      "emailAddress" -> email,
      "password" -> nonEmptyText
    )(LoginUser.apply)(LoginUser.unapply)
      .verifying("There is no user with given e-mail address.", loginUser => checkIfEmailExists(loginUser.emailAddress))
      .verifying("Password does not match", loginUser => checkPasswordMatch(loginUser))
  )

  def checkIfEmailExists(emailAddress: String): Boolean = {
    for (user <- getAllUsers){
      if (user.emailAddress == emailAddress) {
        return true
      }
    }
    return false
  }

  def checkPasswordMatch(user: LoginUser): Boolean = {
    val password = getPasswordForEmail(user.emailAddress)
    if (user.password == password){
      return true
    } else {
      return false
    }
  }

  def getPasswordForEmail(emailAddress: String) : String = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users WHERE emailAddress={emailAddress}").on("emailAddress" -> emailAddress).as(SqlParser.str("password").single)
      }

  }

  def getAllUsers = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users")().map {row =>
        LoginUser(
          id = row[Long]("id"),
          emailAddress = row[String]("emailAddress"),
          password = row[String]("password")
        )
      }.toList

    }
  }

}
