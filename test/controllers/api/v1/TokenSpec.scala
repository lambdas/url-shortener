package controllers.api.v1

import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._

class TokenSpec extends AppSpec {

  "GET /api/v1/token" should "return 403" in {
    val result = get("/api/v1/token")
    result.status should equal (BAD_REQUEST)
  }

}
