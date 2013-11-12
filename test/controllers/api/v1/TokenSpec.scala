package controllers.api.v1

import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj, toJsFieldJsValueWrapper}

class TokenSpec extends AppSpec {

  "GET /api/v1/token" should "return 403" in {
    val result = get("/api/v1/token")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "user_id" -> "required"
      )
    ))
  }
  
  "GET /api/v1/token?user_id=bad" should "return 403" in {
    val result = get("/api/v1/token?user_id=bad")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "user_id" -> "expecting a number"
      )
    ))
  }
  
  "GET /api/v1/token?user_id=42" should "return 403" in {
    val result = get("/api/v1/token?user_id=42")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "secret" -> "required"
      )
    ))
  }
  
  "GET /api/v1/token?user_id=42&secret=secret" should "return 200" in {
    val result = get("/api/v1/token?user_id=42&secret=secret")
    result.status should equal (OK)
  }

}
