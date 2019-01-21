package models

import anorm._
import com.google.common.io.BaseEncoding
import play.api.Play.current
import play.api.data.Forms._
import play.api.data._
import play.api.db.DB

case class LoginForm(id: Long, login: String, emailAddress: String, password: String)

object LoginForm {

  val loginForm = Form(
    mapping(
      "id" -> ignored(0L),
      "login" -> ignored(""),
      "emailAddress" -> email,
      "password" -> nonEmptyText
    )(LoginForm.apply)(LoginForm.unapply)
      .verifying("There is no user with given e-mail address.", loginUser => checkIfEmailExists(loginUser.emailAddress))
      .verifying("Password does not match", loginUser => checkPasswordMatch(loginUser))
  )

  def checkIfEmailExists(emailAddress: String): Boolean = {
    for (user <- getAllUsers) {
      if (user.emailAddress == emailAddress) {
        return true
      }
    }
    return false
  }

  def checkPasswordMatch(user: LoginForm): Boolean = {
    val password = BaseEncoding.base64()
                                .decode(getPasswordForEmail(user.emailAddress))
                                .map(_.toChar)
                                .mkString
    if (user.password == password) {
      return true
    } else {
      return false
    }
  }

  def getPasswordForEmail(emailAddress: String): String = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users WHERE emailAddress={emailAddress}").on("emailAddress" -> emailAddress).as(SqlParser.str("password").single)
    }

  }

  def getAllUsers = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users")().map { row =>
        LoginForm(
          id = row[Long]("id"),
          emailAddress = row[String]("emailAddress"),
          login = row[String]("login"),
          password = row[String]("password")
        )
      }.toList

    }
  }

}
