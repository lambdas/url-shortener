package controllers.api.v1

import anorm.Id
import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj}
import models._

class FolderControllerSpec extends AppSpec {

  "POST /api/v1/folder" should "be secured" in {
    val result = post("/api/v1/folder")
    result.status should equal (UNAUTHORIZED)
  }
  
  it should "return 400 if parameters are missing" in new Fixtures {
    val result = post("/api/v1/folder?token=good-token")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "title" -> arr("This field is required")
      )
    ))
  }
  
  it should "create folder and return new folder id if parameters are valid" in new Fixtures {
    val result = post("/api/v1/folder?token=good-token", obj(
        "title" -> "fun"
    ))
    
    result.status should equal (OK)
    
    val newId = (result.json \ "id").as[Long]
    Folder.findOneByIdAndUserId(newId, user.id.get) should be (
        Some(Folder(Id(newId), "fun", user.id.get))
    )
  }
  
  it should "return 400 if folder with such name already exists" in new Fixtures {
    val result = post("/api/v1/folder?token=good-token", obj(
        "title" -> "existing"
    ))
    
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Folder with such name already exists")
    ))
  }

  class Fixtures {
  
    val user   = User create User("good-secret", "good-token")
    
    val folder = Folder create Folder("existing", user.id.get)
  
  }
  
}
