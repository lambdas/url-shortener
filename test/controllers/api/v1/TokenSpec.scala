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
        "user_id" -> arr("This field is required"),
        "secret"  -> arr("This field is required")
      )
    ))
  }
  
  "GET /api/v1/token?user_id=bad&secret=secret" should "return 403" in {
    val result = get("/api/v1/token?user_id=bad&secret=secret")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "user_id" -> arr("Numeric value expected")
      )
    ))
  }
  
  "GET /api/v1/token?user_id=42&secret=secret" should "return 200" in {
    val result = get("/api/v1/token?user_id=42&secret=secret")
    result.status should equal (OK)
  }

}
