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

  val createForm = Form(
    mapping(
      "title" -> nonEmptyText
    )(Folder.apply)(f => Folder.unapply(f).map(_._2))
  )
    
  def create = Authenticated(parse.json) { request =>
    createForm.bind(request.body).withSuccess { folder =>
      withUniqueFolderTitle(folder.title) {
        Ok(obj("id" -> Folder.create(folder).id.get))
      }
    }
  }
    
  protected def withUniqueFolderTitle(title: String)(f: => Result): Result = {
    if (Folder.findOneByTitle(title).isEmpty)
      f
    else
      BadRequest(Errors.common("Folder with such name already exists"))
  }
  
}
