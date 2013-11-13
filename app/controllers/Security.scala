package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.mvc.Security.AuthenticatedBuilder
import models.User

trait Security {
  
    object Authenticated 
      extends AuthenticatedBuilder(req => getUserFromRequest(req))

    protected def getUserFromRequest(request: RequestHeader): Option[User] = {
      request.queryString.get("token")
        .flatMap(_.headOption)
        .flatMap(User.findOneByToken)
    }
}
