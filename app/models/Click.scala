package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import java.util.Date

case class Click(
    id:       Pk[Long],
    linkId:   Long,
    refferer: String, 
    ip:       String, 
    created:  Date
)

object Click {

    def apply(linkId: Long, refferer: String, ip: String): Click =
      Click(NotAssigned, linkId, refferer, ip, new Date())
  
  val simple =
    get[Pk[Long]]("id")       ~
    get[Long]    ("link_id")  ~
    get[String]  ("refferer") ~
    get[String]  ("ip")       ~
    get[Date]    ("created")  map {
      case id ~ linkId ~ refferer ~ ip ~ created =>
        Click(id, linkId, refferer, ip, created)
    }
    
}