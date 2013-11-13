package controllers.api.v1

import anorm.Id
import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj}
import models._

class ClickControllerSpec extends AppSpec {

  "POST /api/v1/link/:code" should "return 404 if no such link exists" in {
    val result = post(s"/api/v1/link/whatever")
    result.status should equal (NOT_FOUND)
    result.json should equal (obj(
      "errors" -> obj(
        "code" -> arr("Not exists")
      )
    ))
  }
  
  it should "return 400 if parameters are missing" in new Fixtures {
    val result = post(s"/api/v1/link/${link.code}")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "refferer" -> arr("This field is required"),
        "ip"       -> arr("This field is required")
      )
    ))
  }
  
  // TODO: return 400 if parameters are malformed
  
  it should "create click if all parameters are valid" in new Fixtures {
    val result = post(s"/api/v1/link/${link.code}", obj(
        "refferer" -> "http://my-site.com",
        "ip"       -> "8.8.8.8"
    ))
    
    result.status should equal (OK)
    
    result.json should equal (obj(
      "url"       -> link.url,
      "code"      -> link.code,
      "folder_id" -> link.folderId
    ))
    
    val newClick = Click.findByLinkId(link.id.get)(0)
    newClick.refferer should be ("http://my-site.com")
    newClick.ip should be ("8.8.8.8")
  }
  
  class Fixtures {
  
    val me   = User create User("good-secret", "good-token")
    
    val folder = Folder create Folder("existing", me.id.get)
    
    val link = Link create Link("http://url.com", None, me.id.get, Some(folder.id.get))

  }
  
}
