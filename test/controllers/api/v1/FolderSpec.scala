package controllers.api.v1

import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj}

class FolderSpec extends AppSpec {

  "POST /api/v1/folder" should "return 400 if parameters are missing" in {
    val result = post("/api/v1/folder")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "title" -> arr("This field is required")
      )
    ))
  }

}