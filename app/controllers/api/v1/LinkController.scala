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

object LinkController extends Controller with Security {

  def list(offset: Long, limit: Long) = Authenticated { request =>
    Ok(obj())
  }
  
  def create = Authenticated(parse.json) { implicit request =>
    implicit val user = request.user
    createForm.bindFromRequest.withSuccess { link =>
      withUniqueLinkCode(link.code) {
        Ok(toJson(Link.create(link)))
      }
    }
  }
  
  def show(code: String) = Authenticated { request =>
    Ok(obj())
  }
  
  def delete(code: String) = Authenticated { request =>
    Ok(obj())
  }
  
  protected def createForm(implicit user: User) = Form(
    mapping(
      // TODO: Make a real url check
      "url" -> nonEmptyText.verifying(
        "Invalid url",
        (url: String) => url.startsWith("htt")
      ),
      "code"      -> optional(nonEmptyText(5, 10)),
      "folder_id" -> optional(longNumber)
    )(Link.apply(_, _, user.id.get, _))
     (f => Link.unapply(f).map(l => (l._2, Some(l._3), l._5)))
  )
  
  protected def withUniqueLinkCode(code: String)
                                  (f: => Result): Result = {
    if (Link.findOneByCode(code).isEmpty)
      f
    else
      BadRequest(obj(
          "errors" -> obj(
              "code" -> arr("Already exists")
           )
       ))
  }
  
}
