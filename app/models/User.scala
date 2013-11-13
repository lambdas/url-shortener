package models

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

case class User(
    id:     Pk[Long],
    secret: String,   // TODO: Encrypt me
    token:  String    // TODO: Move me to Redis
)

object User {
  
  def apply(secret: String, token: String): User = 
    User(NotAssigned, secret, token)
  
  val simple =
    get[Pk[Long]]("id")     ~
    get[String]  ("secret") ~
    get[String]  ("token")  map {
      case id ~ secret ~ token =>
        User(id, secret, token)
    }
  
  def findOneByIdAndSecret(id: Long, secret: String): Option[User] = 
    DB.withConnection { implicit connection =>
      SQL(
        s"""
           |SELECT * FROM users
           |  WHERE id = {id}
           |  AND secret = {secret}
         """.stripMargin
      ).on(
        'id -> id,
        'secret -> secret
      ).as(simple.singleOpt)
    }
  
  def create(user: User): User = DB.withTransaction {
    implicit connection =>
      
      val id: Long = user.id.getOrElse {
        SQL("select nextval('users_id_seq')").as(scalar[Long].single)
      }

      SQL(
        """
           |INSERT INTO users (id, secret, token)
           |  VALUES ({id}, {secret}, {token})
        """.stripMargin
      ).on(
        'id     -> id,
        'secret -> user.secret,
        'token  -> user.token
      ).executeInsert()

      user.copy(id = Id(id))
  }
  
}
