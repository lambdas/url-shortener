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
  
  def create(code: String) = Action(parse.json) { implicit request =>
    withLink(code) { link =>
      createForm(link.id.get).bindFromRequest.withSuccess { click =>
        Click.create(click)
        Ok(toJson(link))
      }
    }
  }
  
  protected def createForm(linkId: Long) = Form(
    mapping(
      // TODO: Validate url
      "refferer" -> nonEmptyText,
      // TODO: Validate ip
      "ip"       -> nonEmptyText
    )(Click.apply(linkId, _, _))
     (f => Click.unapply(f).map(l => (l._3, l._4)))
  )
  
  def withLink(code: String)
              (f: Link => Result): Result = {
    Link.findOneByCode(code).map(f)
      .getOrElse(NotFound(obj(
          "errors" -> obj(
              "code" -> arr("Not exists")
           )
       )))
  }
  
}
