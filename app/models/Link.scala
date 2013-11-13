package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class Link(
    id:       Pk[Long],
    code:     String,
    userId:   Long,
    folderId: Option[Long]
)

object Link {
  
  def apply(code: String, userId: Long, folderId: Option[Long]): Link =
    Link(NotAssigned, code, userId, folderId)
  
  val simple =
    get[Pk[Long]]    ("id")       ~
    get[String]      ("code")     ~
    get[Long]        ("userId")   ~
    get[Option[Long]]("folderId") map {
      case id ~ code ~ userId ~ folderId =>
        Link(id, code, userId, folderId)
    }
  
  
}
