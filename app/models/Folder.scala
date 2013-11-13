package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Folder(
    id:    Pk[Long],
    title: String
)

object Folder {
  
  def apply(title: String): Folder = Folder(NotAssigned, title)
  
  val simple =
    get[Pk[Long]]("id")    ~
    get[String]  ("title") map {
      case id ~ title =>
        Folder(id, title)
    }
  
  def findOneById(id: Long): Option[Folder] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM folders
           |  WHERE id = {id}
         """.stripMargin
      ).on(
        'id -> id
      ).as(simple.singleOpt)
    }
  
  def findOneByTitle(title: String): Option[Folder] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM folders
           |  WHERE title = {title}
         """.stripMargin
      ).on(
        'title -> title
      ).as(simple.singleOpt)
    }
  
  def create(folder: Folder): Folder = DB.withTransaction {
    implicit connection =>
      
      val id: Long = folder.id.getOrElse {
        SQL("select nextval('folders_id_seq')").as(scalar[Long].single)
      }

      SQL(
        """
           |INSERT INTO folders (id, title)
           |  VALUES ({id}, {title})
        """.stripMargin
      ).on(
        'id    -> id,
        'title -> folder.title
      ).executeInsert()

      folder.copy(id = Id(id))
  }
  
}
