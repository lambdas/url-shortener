package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.obj
import models._
import controllers.RichForm._

object Token extends Controller {

  val createForm = Form(
    tuple(
      "user_id" -> longNumber,
      "secret"  -> nonEmptyText
    )
  )

  def create = Action { request =>
    createForm.bindFromRequest(request.queryString).withSuccess {
      case (userId, secret) => 
        User.findOneByIdAndSecret(userId, secret)
          .map(u => Ok(obj("token" -> u.token)))
          .getOrElse(
              Unauthorized(commonErrorAsJson("Wrong user id or secret"))
          )
    }
  }

  // TODO: Move me somewhere
  def commonErrorAsJson(messages: String*) = obj(
    "errors"         -> obj(),
    "error_messages" -> messages
  )
}
