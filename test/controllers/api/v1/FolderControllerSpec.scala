package controllers.api.v1

import anorm.Id
import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj}
import models._

class FolderControllerSpec extends AppSpec {

  "POST /api/v1/folder" should "return 400 if parameters are missing" in {
    val result = post("/api/v1/folder")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "title" -> arr("This field is required")
      )
    ))
  }
  
  it should "create folder and return new folder id if parameters are valid" in {
    val result = post("/api/v1/folder", obj(
        "title" -> "fun"
    ))
    
    result.status should equal (OK)
    
    val newId = (result.json \ "id").as[Long]
    Folder.findOneById(newId) should be (Some(Folder(Id(newId), "fun")))
  }
  
  it should "return 400 if folder with such name already exists" in new Fixtures {
    val result = post("/api/v1/folder", obj(
        "title" -> "fun"
    ))
    
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Folder with such name already exists")
    ))
  }

  class Fixtures {
  
    val folder = Folder create Folder("fun")
  
  }
  
}
