package controllers

import play.api.libs.json.Json

object Errors {

  def common(messages: String*) = Json.obj(
    "errors"         -> Json.obj(),
    "error_messages" -> messages
  )
  
}
