package models

import anorm._
import play.api.Play.current
import play.api.db.DB
import play.api.data.Forms._
import play.api.data._

case class User(id: Long, emailAddress: String, password: String, repeatPassword: String)

object User {

  val createAccountForm = Form(
    mapping(
      "id" -> ignored(0L),
      "emailAddress" -> email,
      "password" -> nonEmptyText,
      "repeatPassword" -> nonEmptyText
    )(User.apply)(User.unapply)
      .verifying("User with such a name already exists", user => emailAlreadyInDatabaseCheck(user.emailAddress))
      .verifying("Passwords are not identical.", user => samePasswordsCheck(user.password, user.repeatPassword))
      .verifying("There has to be at least 5 characters", user => passwordLengthCheck(user.password))
      .verifying("There has to be at least 1 capital character", user => passwordCapitalLetterCheck(user.password))
      .verifying("There has to be at least 1 special character", user => passwordSpecialSignCheck(user.password))

  )

  def getAllEmailAddresses : List[String] = {

    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM users").as(SqlParser.str("emailAddress").*)

    }
  }

  def createUser(user: User): Unit = {
    DB.withConnection { implicit connection =>

        SQL("INSERT INTO users(emailAddress, password) VALUES ({emailAddress}, {password})").on(
          "emailAddress" -> user.emailAddress,
          "password" -> user.password
        ).execute()

    }
  }

  def emailAlreadyInDatabaseCheck(emailAddress: String): Boolean = {
    for (name <- getAllEmailAddresses){
      if (emailAddress == name){
        return false
      }
    }
    return true
  }



  def samePasswordsCheck(password: String, repeatPassword: String) : Boolean = {
    if (password == repeatPassword){
      return true
    } else {
      return false
    }
  }

  def passwordLengthCheck(password: String) : Boolean = {
    if (password.length >= 5){
      return true
    } else {
      return false
    }
  }

  def passwordCapitalLetterCheck(password: String) : Boolean = {
    if (password.exists(_.isUpper)){
      return true
    } else {
      return false
    }
  }

  def passwordSpecialSignCheck(password: String) : Boolean = {
    val ordinary = (('a' to 'z') ++ ('A' to 'Z') ++ ('0' to '9')).toSet
    if (password.forall(ordinary.contains(_))){
      return false
    } else {
      return true
    }
  }


}
