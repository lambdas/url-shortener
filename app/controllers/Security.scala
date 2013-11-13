package controllers

import play.api._
import play.api.mvc._
import scala.concurrent.Future
import play.api.mvc.Security.AuthenticatedBuilder
import models.User
import play.api.mvc.Results.Unauthorized

trait Security {
  
  object Authenticated 
    extends AuthenticatedBuilder(getUserFromRequest, onUnauthorized)

  protected def getUserFromRequest(request: RequestHeader): Option[User] = {
    request.queryString.get("token")
      .flatMap(_.headOption)
      .flatMap(User.findOneByToken)
  }
  
  protected def onUnauthorized(request: RequestHeader): SimpleResult = {
    Unauthorized(Errors.common("Unauthorized"))
  }
  
}
