package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Folder(
    id:     Pk[Long],
    title:  String,
    userId: Long
)

object Folder {
  
  def apply(title: String, userId: Long): Folder =
    Folder(NotAssigned, title, userId)
  
  val simple =
    get[Pk[Long]]("id")      ~
    get[String]  ("title")   ~
    get[Long]    ("user_id") map {
      case id ~ title ~ userId =>
        Folder(id, title, userId)
    }
  
  def findOneByIdAndUserId(id: Long, userId: Long): Option[Folder] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM folders
           |  WHERE id = {id}
           |  AND user_id = {userId}
         """.stripMargin
      ).on(
        'id     -> id,
        'userId -> userId
      ).as(simple.singleOpt)
    }
  
  def findOneByTitleAndUserId(title: String, userId: Long): Option[Folder] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM folders
           |  WHERE title = {title}
           |  AND user_id = {userId}
         """.stripMargin
      ).on(
        'title  -> title,
        'userId -> userId
      ).as(simple.singleOpt)
    }
  
  def create(folder: Folder): Folder = DB.withTransaction {
    implicit connection =>
      
      val id: Long = folder.id.getOrElse {
        SQL("select nextval('folders_id_seq')").as(scalar[Long].single)
      }

      SQL(
        """
           |INSERT INTO folders (id, title, user_id)
           |  VALUES ({id}, {title}, {userId})
        """.stripMargin
      ).on(
        'id     -> id,
        'title  -> folder.title,
        'userId -> folder.userId
      ).executeInsert()

      folder.copy(id = Id(id))
  }
  
}
