package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.obj
import models._
import controllers.RichForm._
import controllers.Errors
import controllers.Security
import play.api.libs.json._
import play.api.libs.functional.syntax._

object LinkController extends Controller with Security {

  def list(offset: Long, limit: Long) = Authenticated { request =>
    Ok(obj())
  }
  
  def create = Authenticated(parse.json) { implicit request =>
    implicit val user = request.user
    createForm.bindFromRequest.withSuccess { link =>
      Ok(Json.toJson(Link.create(link)))
    }
  }
  
  def show(code: String) = Authenticated { request =>
    Ok(obj())
  }
  
  def delete(code: String) = Authenticated { request =>
    Ok(obj())
  }
  
  protected implicit val linkWrites = (
    (__ \ "url")      .write[String] ~
    (__ \ "code")     .write[String] ~
    (__ \ "folder_id").writeNullable[Long]
  )((l: Link) => (l.url, l.code, l.folderId))
  
  protected def createForm(implicit user: User) = Form(
    mapping(
      // TODO: Make a real url check
      "url"       -> nonEmptyText.verifying(
          "Invalid url",
          (url: String) => url.startsWith("htt")
      ),
      "code"      -> optional(nonEmptyText(5, 10)).transform(
          _.getOrElse("qwe").map(_.toLower),
          (code: String) => Some(code)
      ),
      "folder_id" -> optional(longNumber)
    )(Link.apply(_, _, user.id.get, _))
     (f => Link.unapply(f).map(l => (l._2, l._3, l._5)))
  )
  
}