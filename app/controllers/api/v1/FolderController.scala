package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.{obj, arr}
import models._
import controllers.RichForm._
import controllers.Errors
import controllers.Security
import play.api.libs.json._
import play.api.libs.functional.syntax._

object FolderController extends Controller with Security {
    
  def list(offset: Long, limit: Long) = Authenticated { request =>
    Ok(Json.toJson(Folder.list(offset, limit, request.user.id.get)))
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
    Ok(arr())
  }
  
  def delete(id: Long) = Authenticated { request =>
    implicit val user = request.user
    withFolder(id) { folder =>
      Folder.delete(id, user.id.get)
      Ok
    }
  }
  
  protected implicit val folderWrites = (
    (__ \ "id")   .write[Long] ~
    (__ \ "title").write[String]
  )((f: Folder) => (f.id.get, f.title))
  
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
      .getOrElse(NotFound(Errors.common("No folder with such id")))
  }
  
}
