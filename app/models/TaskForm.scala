package models

import anorm.{SQL, SqlParser}
import play.api.db.DB
import play.api.Play.current
import play.api.data._
import play.api.data.Forms._

case class TaskForm(id: Long, userId: Long, information: String)

object TaskForm {

  val form = Form(
    mapping(
      "id" -> ignored(0L),
      "userId" -> ignored(0L),
      "information" -> nonEmptyText
    )(TaskForm.apply)(TaskForm.unapply)
  )

  def delete(id: Long) {
    DB.withConnection { implicit connection =>
      SQL("DELETE FROM data WHERE id={id}").on("id" -> id).execute()
    }
  }


  def getAllInformationsForUser(currentEmailAddress: Option[String]) = {
    DB.withConnection { implicit connection =>
      val contacts = SQL("SELECT * FROM data WHERE userId = {userId}").on("userId" -> getIdByEmail(currentEmailAddress))().map { row =>
        TaskForm(
          id = row[Long]("id"),
          userId = row[Long]("userId"),
          information = row[String]("information")
        )
      }

      contacts.toList
    }
  }

  def getIdByEmail(currentEmailAddress: Option[String]): Integer = {
    DB.withConnection { implicit connection =>
      val id: Int = SQL(
        """
          select * from users
          where emailAddress = {emailAddress}
          """)
        .on("emailAddress" -> currentEmailAddress).as(SqlParser.int("id").single)

      id
    }
  }

  def createTask(information: TaskForm, currentUserMail: Option[String]) {

    DB.withConnection { implicit connection =>
      SQL("INSERT INTO data(userid, information) VALUES({userId}, {information})").on(
        "userId" -> getIdByEmail(currentUserMail),
        "information" -> information.information
      ).execute()
    }
  }

  def getTask(id: Long) = {
    DB.withConnection { implicit connection =>
      SQL("SELECT * FROM data WHERE id={id}").on("id" -> id)().headOption.map { row =>
        TaskForm(
          id = row[Long]("id"),
          userId = row[Long]("userId"),
          information = row[String]("information")
        )
      }
    }
  }

  def updateInformation(id: Long, userInformation: TaskForm) {
    DB.withConnection { implicit connection =>
      SQL("UPDATE data SET information = {information} WHERE id={id}").on(
        "id" -> id,
        "userId" -> userInformation.userId,
        "information" -> userInformation.information
      ).execute()
    }
  }

  def getLoginByEmail(currentEmail: String): String = {
    DB.withConnection {implicit connection =>
      val login: String = SQL (
      """
        select * from users
        where emailAddress = {emailAddress}
        """)
      .on ("emailAddress" -> currentEmail).as (SqlParser.str("login").single)
      login
    }
  }

}
