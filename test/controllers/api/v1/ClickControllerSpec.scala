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
        "referer" -> arr("This field is required"),
        "ip"       -> arr("This field is required")
      )
    ))
  }
  
  // TODO: return 400 if parameters are malformed
  
  it should "create click if all parameters are valid" in new Fixtures {
    val result = post(s"/api/v1/link/${link.code}", obj(
        "referer" -> "http://my-site.com",
        "ip"       -> "8.8.8.8"
    ))
    
    result.status should equal (OK)
    
    result.json should equal (obj(
      "url"         -> link.url,
      "code"        -> link.code,
      "folder_id"   -> link.folderId,
      "click_count" -> Link.findOneByCode(link.code).get.clickCount
    ))
    
    val newClick = Click.findByLinkId(link.id.get).find(_.refferer == "http://my-site.com").get
    newClick.ip should be ("8.8.8.8")
  }
  
  it should "increment click count" in new Fixtures {
    val oldClickCount = Link.findOneByCode(link.code).get.clickCount
    
    val result = post(s"/api/v1/link/${link.code}", obj(
        "referer" -> "http://my-site.com",
        "ip"       -> "8.8.8.8"
    ))
    
    Link.findOneByCode(link.code).get.clickCount should be (oldClickCount + 1)
  }
  
  "GET /api/v1/link/:code/click" should "be secured" in new Fixtures {
    val result = get(s"/api/v1/link/${link.code}/click")
    result.status should equal (UNAUTHORIZED)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Unauthorized")
    ))
  }
  
  it should "return 404 if no such link found" in new Fixtures {
    val result = get(s"/api/v1/link/whatever/click?token=${me.token}")
    result.status should equal (NOT_FOUND)
    result.json should equal (obj(
      "errors" -> obj(
        "code" -> arr("Not exists")
      )
    ))
  }
  
  it should "return clicks" in new Fixtures {
    val result = get(s"/api/v1/link/${link.code}/click?token=${me.token}")
    result.status should equal (OK)
    result.json should equal (arr(
      obj(
        "referer" -> click_1.refferer,
        "ip"       -> click_1.ip,
        "created"  -> click_1.created
      ), obj(
        "referer" -> click_2.refferer,
        "ip"       -> click_2.ip,
        "created"  -> click_2.created
      )
    ))
  }
  
  class Fixtures {
  
    val me   = User create User("good-secret", "good-token")
    
    val folder = Folder create Folder("existing", me.id.get)
    
    val link = Link create Link("http://url.com", None, me.id.get, Some(folder.id.get))

    val click_1 = Click create Click(link.id.get, "http://a.com", "8.8.8.8")
    val click_2 = Click create Click(link.id.get, "http://b.com", "9.9.9.9")
    
  }
  
}
