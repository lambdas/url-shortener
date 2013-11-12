package controllers.api.v1

import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.Json.{obj}

object Token extends Controller {

  val createForm = Form(
    tuple(
      "user_id" -> longNumber,
      "secret" -> nonEmptyText
    )
  )

  def create = Action { request =>
    withForm(createForm.bindFromRequest(request.queryString)) {
      case (userId, secret) => Ok("")
    }
  }

  // TODO: Move me somewhere
  protected def withForm[A](form: Form[A])
                           (onSuccess: A => Result): Result =
    form.fold(
      errors => BadRequest(obj("errors" -> errors.errorsAsJson)),
      onSuccess
    )
  
}
