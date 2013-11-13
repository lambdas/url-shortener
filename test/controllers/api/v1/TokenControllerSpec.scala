package controllers.api.v1

import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj}
import models._

class TokenControllerSpec extends AppSpec {

  "GET /api/v1/token" should "return 400 if parameters are missing" in {
    val result = get("/api/v1/token")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "user_id" -> arr("This field is required"),
        "secret"  -> arr("This field is required")
      )
    ))
  }
  
  it should "return 400 if user_id is not a number" in {
    val result = get("/api/v1/token?user_id=bad-id&secret=wrong-secret")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "user_id" -> arr("Numeric value expected")
      )
    ))
  }
  
  it should "return 401 if wrong credentials passed" in {
    val result = get("/api/v1/token?user_id=42&secret=wrong-secret")
    result.status should equal (UNAUTHORIZED)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Wrong user id or secret")
    ))
  }
  
  it should "return 200 and token if valid credentials passed" in new Fixtures {
    val result = get(s"/api/v1/token?user_id=${user.id.get}&secret=${user.secret}")
    result.status should equal (OK)
    result.json should equal (obj(
      "token" -> user.token
    ))
  }

  class Fixtures {
  
    val user = User create User("good-secret", "good-token")
  
  }
  
}
