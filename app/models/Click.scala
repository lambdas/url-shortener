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
  
  def findByLinkId(linkId: Long): Seq[Click] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM clicks
           |  WHERE link_id = {linkId}
         """.stripMargin
      ).on(
        'linkId -> linkId
      ).as(simple *)
    }
  
  def create(click: Click): Click = DB.withTransaction {
    implicit connection =>
      
      val id: Long = click.id.getOrElse {
        SQL("select nextval('clicks_id_seq')").as(scalar[Long].single)
      }

      SQL(
        """
           |INSERT INTO clicks (id, link_id, refferer, ip, created)
           |  VALUES ({id}, {linkId}, {refferer}, {ip}, {created})
        """.stripMargin
      ).on(
        'id       -> id,
        'linkId   -> click.linkId,
        'refferer -> click.refferer,
        'ip       -> click.ip,
        'created  -> click.created
      ).executeInsert()

      click.copy(id = Id(id))
  }
  
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