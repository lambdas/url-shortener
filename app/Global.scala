import errors._
import play.api.PlayException.ExceptionSource
import play.api._
import play.api.mvc._
import play.api.Play.current
import play.api.mvc.Results._
import play.api.libs.json.Json.{arr, obj}
import scala.concurrent.Future

object Global extends GlobalSettings {

  override def onError(request: RequestHeader, ex: Throwable) = {
    ex.asInstanceOf[ExceptionSource].cause match {
      case InvalidArgumentsError(arg, desc) => Future.successful(
        BadRequest(obj(
          "errors" -> obj(
            arg -> desc))))
      case _                                => super.onError(request, ex)
    }
  }

}