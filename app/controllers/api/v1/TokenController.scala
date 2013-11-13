package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.obj
import models._
import controllers.RichForm._
import controllers.Errors

object TokenController extends Controller {

  def auth = Action { request =>
    authForm.bindFromRequest(request.queryString).withSuccess {
      case (userId, secret) =>
        withAuthenticatedUser(userId, secret) { u =>
          Ok(obj("token" -> u.token))
        }
    }
  }

  protected val authForm = Form(
    tuple(
      "user_id" -> longNumber,
      "secret"  -> nonEmptyText
    )
  )
  
  protected def withAuthenticatedUser(userId: Long, secret: String)
                                     (f: User => Result): Result = {
    User.findOneByIdAndSecret(userId, secret).map(f).getOrElse(
      Unauthorized(Errors.common("Wrong user id or secret"))
    )
  }
  
}
