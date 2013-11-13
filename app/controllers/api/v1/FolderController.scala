package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.{obj, arr, toJson}
import models._
import controllers.RichForm._
import controllers.Errors
import controllers.Security

object FolderController extends Controller with Security {
    
  def list(offset: Long, limit: Long) = Authenticated { request =>
    Ok(toJson(Folder.list(offset, limit, request.user.id.get)))
  }
  
  def create = Authenticated(parse.json) { implicit request =>
    implicit val user = request.user
    createForm.bindFromRequest.withSuccess { folder =>
      withUniqueFolderTitle(folder.title) {
        Ok(obj("id" -> Folder.create(folder).id.get))
      }
    }
  }
  
  def show(id: Long) = Authenticated { request =>
    implicit val user = request.user
    withFolder(id) { folder =>
      Ok(toJson(Link.findByFolderId(id)))
    }
  }
  
  def delete(id: Long) = Authenticated { request =>
    implicit val user = request.user
    withFolder(id) { folder =>
      Link.deleteWithFolderId(id, user.id.get)
      Folder.delete(id, user.id.get)
      Ok(obj())
    }
  }
  
  protected def createForm(implicit user: User) = Form(
    mapping(
      "title" -> nonEmptyText
    )(Folder.apply(_, user.id.get))(f => Folder.unapply(f).map(_._2))
  )
  
  protected def withUniqueFolderTitle(title: String)
                                     (f: => Result)
                                     (implicit user: User): Result = {
    if (Folder.findOneByTitleAndUserId(title, user.id.get).isEmpty)
      f
    else
      BadRequest(Errors.common("Folder with such name already exists"))
  }
  
  protected def withFolder(id: Long)
                          (f: Folder => Result)
                          (implicit user: User): Result = {
    Folder.findOneByIdAndUserId(id, user.id.get).map(f)
      .getOrElse(NotFound(obj(
          "errors" -> obj(
              "id" -> arr("Not exists")
           )
       )))
  }
  
}
