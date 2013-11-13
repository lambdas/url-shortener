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
  
}
