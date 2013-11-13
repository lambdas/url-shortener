package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._

case class Link(
    id:       Pk[Long],
    url:      String,
    code:     String,
    userId:   Long,
    folderId: Option[Long]
)

object Link {
  
  def apply(url: String, code: Option[String], userId: Long, folderId: Option[Long]): Link =
    Link(NotAssigned, url, code.getOrElse(randomCode), userId, folderId)
  
  val simple =
    get[Pk[Long]]    ("id")        ~
    get[String]      ("url")       ~
    get[String]      ("code")      ~
    get[Long]        ("user_id")   ~
    get[Option[Long]]("folder_id") map {
      case id ~ url ~ code ~ userId ~ folderId =>
        Link(id, url, code, userId, folderId)
    }
  
  def findOneByCode(code: String): Option[Link] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM links
           |  WHERE code = {code}
         """.stripMargin
      ).on(
        'code -> code
      ).as(simple.singleOpt)
    }
  
  def findOneByCodeAndUserId(code: String, userId: Long): Option[Link] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM links
           |  WHERE code = {code}
           |  AND user_id = {userId}
         """.stripMargin
      ).on(
        'code   -> code,
        'userId -> userId
      ).as(simple.singleOpt)
    }
  
  def findByFolderId(folderId: Long): Seq[Link] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM links
           |  WHERE folder_id = {folderId}
         """.stripMargin
      ).on(
        'folderId -> folderId
      ).as(simple *)
    }
  
  def list(offset: Long, limit: Long, userId: Long): Seq[Link] =
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM links
           |  WHERE user_id = {userId}
           |  OFFSET {offset}
           |  LIMIT {limit}
         """.stripMargin
      ).on(
        'userId -> userId,
        'offset -> offset,
        'limit  -> limit
      ).as(simple *)
    }
  
  def create(link: Link): Link = DB.withTransaction {
    implicit connection =>
      
      val id: Long = link.id.getOrElse {
        SQL("select nextval('links_id_seq')").as(scalar[Long].single)
      }

      SQL(
        """
           |INSERT INTO links (id, url, code, user_id, folder_id)
           |  VALUES ({id}, {url}, {code}, {userId}, {folderId})
        """.stripMargin
      ).on(
        'id       -> id,
        'url      -> link.url,
        'code     -> link.code,
        'userId   -> link.userId,
        'folderId -> link.folderId
      ).executeInsert()

      link.copy(id = Id(id))
  }
  
  def deleteWithFolderId(folderId: Long, userId: Long): Unit = DB.withTransaction {
    implicit connection =>
      
      SQL(
          """
             |DELETE FROM links
             |  WHERE folder_id = {folderId}
             |  AND user_id = {userId}
          """.stripMargin
        ).on(
          'folderId -> folderId,
          'userId   -> userId
        ).executeUpdate()
  }
  
  def deleteByCode(code: String, userId: Long): Unit = DB.withTransaction {
    implicit connection =>
      
      SQL(
          """
             |DELETE FROM links
             |  WHERE code = {code}
             |  AND user_id = {userId}
          """.stripMargin
        ).on(
          'code   -> code,
          'userId -> userId
        ).executeUpdate()
  }
  
  // TODO: Check for collision
  def randomCode: String = UUID.randomUUID().toString().take(8)
  
  implicit val linkWrites = (
    (__ \ "url")      .write[String] ~
    (__ \ "code")     .write[String] ~
    (__ \ "folder_id").writeNullable[Long]
  )((l: Link) => (l.url, l.code, l.folderId))
  
}
