package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Link(
    id:       Pk[Long],
    url:      String,
    code:     String,
    userId:   Long,
    folderId: Option[Long]
)

object Link {
  
  def apply(url: String, code: String, userId: Long, folderId: Option[Long]): Link =
    Link(NotAssigned, url, code, userId, folderId)
  
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
  
}
