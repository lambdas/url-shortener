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
    Folder.findOneByIdAndUserId(newId, me.id.get) should be (
        Some(Folder(Id(newId), "fun", me.id.get))
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
  
  "GET /api/v1/folder" should "be secured" in {
    val result = get("/api/v1/folder")
    result.status should equal (UNAUTHORIZED)
  }
  
  it should "return my folders" in new Fixtures {
    val result = get(s"/api/v1/folder?token=${me.token}")
    result.status should equal (OK)
    result.json should equal (arr(
      obj(
        "id"    -> mineFolder_1.id.get,
        "title" -> mineFolder_1.title
      ), obj(
        "id"    -> mineFolder_2.id.get,
        "title" -> mineFolder_2.title
      )
    ))
  }

  class Fixtures {
  
    val me   = User create User("good-secret", "good-token")
    val john = User create User("good-secret", "john-token")
    
    val mineFolder_1 = Folder create Folder("existing", me.id.get)
    val mineFolder_2 = Folder create Folder("another existing", me.id.get)
    
    val johnsFolder  = Folder create Folder("johns folder", john.id.get)
  
  }
  
}
