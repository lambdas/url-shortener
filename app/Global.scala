import play.api._
import play.api.mvc._
import play.api.mvc.Results.BadRequest
import scala.concurrent.Future
import controllers.Errors

object Global extends GlobalSettings {

  // TODO: Ugly format, we can do better
  override def onBadRequest(request: RequestHeader, error: String): Future[SimpleResult] = {
    Future.successful(BadRequest(Errors.common(error)))
  }
  
}