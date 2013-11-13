package test

import play.api.test.Helpers._
import play.api.libs.ws.{WS, Response}
import play.api.libs.json.JsValue
import play.api.libs.json.Json.obj

object ControllerUtils {

  def get(url: String): Response = {
    await(WS.url(s"http://localhost:3333$url").get)
  }

  def post(url: String, body: JsValue = obj()): Response = {
    await(WS.url(s"http://localhost:3333$url").post(body))
  }

}