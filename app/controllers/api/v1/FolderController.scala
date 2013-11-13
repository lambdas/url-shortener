package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.{obj}
import models._
import controllers.RichForm._
import controllers.Errors
import controllers.Security

object FolderController extends Controller with Security {

  protected def createForm(implicit user: User) = Form(
    mapping(
      "title" -> nonEmptyText
    )(Folder.apply(_, user.id.get))(f => Folder.unapply(f).map(_._2))
  )
    
  def list(offset: Long, limit: Long) = Authenticated(parse.json) { request =>
    Ok("")
  }
  
  def create = Authenticated(parse.json) { implicit request =>
    implicit val user = request.user
    createForm.bindFromRequest.withSuccess { folder =>
      withUniqueFolderTitle(folder.title) {
        Ok(obj("id" -> Folder.create(folder).id.get))
      }
    }
  }
    
  protected def withUniqueFolderTitle(title: String)
                                     (f: => Result)
                                     (implicit user: User): Result = {
    if (Folder.findOneByTitleAndUserId(title, user.id.get).isEmpty)
      f
    else
      BadRequest(Errors.common("Folder with such name already exists"))
  }
  
}
