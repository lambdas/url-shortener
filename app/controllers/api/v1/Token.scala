package controllers.api.v1

import play.api._
import play.api.mvc._
import errors._

object Token extends Controller {

  def create = Action { request =>
    val userId = request.queryString.get("user_id").flatMap(_.headOption)
      .orElse(throw InvalidArgumentsError("user_id", "required"))
      .flatMap(toLong _)
      .getOrElse(throw InvalidArgumentsError("user_id", "expecting a number"))
    val secret = request.queryString.get("secret")
      .orElse(throw InvalidArgumentsError("secret", "required"))
    Ok("FINE")
  }

  // TODO: Move me somewhere
  protected def toLong(s: String): Option[Long] = {
    try {
      Some(s.toLong)
    } catch {
      case e:Exception => None
    }
  }
  
}
