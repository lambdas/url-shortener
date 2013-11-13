package controllers

import play.api._
import play.api.mvc._
import play.api.mvc.Results.BadRequest
import play.api.libs.json.Json.obj
import play.api.data._

class RichForm[A](underlying: Form[A]) {
  
  def withSuccess(f: A => Result): Result =
    underlying.fold(
      errors => BadRequest(obj("errors" -> errors.errorsAsJson)),
      f
    )

}

object RichForm {
  
  implicit def toRichForm[A](underlying: Form[A]): RichForm[A] =
    new RichForm(underlying)

}
