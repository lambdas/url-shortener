package test

import play.api.test.Helpers._
import play.api.libs.ws.{WS, Response}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.obj
import play.api.libs.json.Json.{arr, obj}

object ControllerUtils {

  def get(url: String): Response = {
    await(WS.url(s"http://localhost:3333$url").get)
  }

  def post(url: String, body: JsValue = obj()): Response = {
    await(WS.url(s"http://localhost:3333$url").post(body))
  }

  def delete(url: String): Response = {
    await(WS.url(s"http://localhost:3333$url").delete)
  }
  
  val unauthorizedError = obj(
    "errors"         -> obj(),
    "error_messages" -> arr("Unauthorized")
  )
  
  // TODO: It shows invalid file:line number, but still understandable
  def assertUnauthorized(result: Response) {
    import org.scalatest.Matchers._
    
    result.status should equal (UNAUTHORIZED)
    result.json should equal (unauthorizedError)
  }
}