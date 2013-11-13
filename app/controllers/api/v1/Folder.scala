package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.{obj}
import models.{Folder => FolderModel}
import controllers.RichForm._

object Folder extends Controller {

  val createForm = Form(
    mapping(
      "title" -> nonEmptyText
    )(FolderModel.apply)(f => FolderModel.unapply(f).map(_._2))
  )
  
  def create = Action(parse.json) { request =>
    createForm.bind(request.body).withSuccess { folder =>
      Ok(obj())
    }
  }
    
}