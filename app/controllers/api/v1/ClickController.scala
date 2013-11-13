package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.{toJson, arr, obj}
import models._
import controllers.RichForm._
import controllers.Errors
import controllers.Security

object ClickController extends Controller with Security {

  def list(code: String, offset: Long, limit: Long) = Authenticated { request =>
    Ok //(toJson(Clicks.list(code, offset, limit, request.user.id.get)))
  }
  
  def create(code: String) = Authenticated(parse.json) { implicit request =>
    //implicit val user = request.user
    //createForm.bindFromRequest.withSuccess { link =>
    //  withUniqueLinkCode(link.code) {
    //    Ok(toJson(Link.create(link)))
    //  }
    //}
    Ok
  }
  
}
