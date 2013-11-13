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

object LinkController extends Controller with Security {

  def list(offset: Long, limit: Long) = Authenticated { request =>
    Ok
  }
  
  def create = Authenticated(parse.json) { request =>
    Ok
  }
  
  def show(code: String) = Authenticated { request =>
    Ok
  }
  
  def delete(code: String) = Authenticated { request =>
    Ok
  }
  
}