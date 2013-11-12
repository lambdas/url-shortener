package test

import play.api.test.Helpers._
import play.api.libs.ws.{WS, Response}

object ControllerUtils {

  def get(url: String): Response = {
    await(WS.url(s"http://localhost:3333$url").get)
  }

}