package models

import anorm._
import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import play.api.Play.current
import play.api.db.DB
import play.api.data.Forms._
import play.api.data._

case class CreateUserForm(id: Long, login: String, emailAddress: String, password: String, repeatPassword: String)

object CreateUserForm {

  val createAccountForm = Form(
    mapping(
      "id" -> ignored(0L),
      "login" -> nonEmptyText,
      "emailAddress" -> email,
      "password" -> nonEmptyText,
      "repeatPassword" -> nonEmptyText
    )(CreateUserForm.apply)(CreateUserForm.unapply)
      .verifying("User with such a email already exists", user => emailAlreadyInDatabaseCheck(user.emailAddress))
      .verifying("User with such a login already exists", user => loginAlreadyInDatabaseCheck(user.login))
      .verifying("Passwords are not identical.", user => samePasswordsCheck(user.password, user.repeatPassword))
      .verifying("There has to be at least 5 characters", user => passwordLengthCheck(user.password))
      .verifying("There has to be at least 1 capital character", user => passwordCapitalLetterCheck(user.password))
      .verifying("There has to be at least 1 special character", user => passwordSpecialSignCheck(user.password))

  )

  def getAllEmailAddresses: List[String] = {

    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users").as(SqlParser.str("emailAddress").*)

    }
  }

  def getAllLogins: List[String] = {

    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users").as(SqlParser.str("login").*)

    }
  }

  def createUser(user: CreateUserForm): Unit = {
    DB.withConnection { implicit connection =>

      SQL("INSERT INTO users(login, emailAddress, password) VALUES ({login}, {emailAddress}, {password})").on(
        "login" -> user.login,
        "emailAddress" -> user.emailAddress,
        "password" -> BaseEncoding.base64()
          .encode(user.password.getBytes(Charsets.UTF_8))
      ).execute()

    }
  }


  def loginAlreadyInDatabaseCheck(login: String): Boolean = {
    return !getAllLogins.contains(login)
  }


  def emailAlreadyInDatabaseCheck(emailAddress: String): Boolean = {
    return !getAllEmailAddresses.contains(emailAddress)
  }


  def samePasswordsCheck(password: String, repeatPassword: String): Boolean = {
    return password == repeatPassword
  }

  def passwordLengthCheck(password: String): Boolean = {
    return password.length >= 5
  }

  def passwordCapitalLetterCheck(password: String): Boolean = {
    return password.exists(_.isUpper)
  }

  def passwordSpecialSignCheck(password: String): Boolean = {
    val ordinary = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toSet
    if (password.forall(ordinary.contains(_))) {
      return false
    } else {
      return true
    }
  }


}
