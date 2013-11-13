package controllers.api.v1

import anorm.Id
import test.AppSpec
import test.ControllerUtils._
import play.api.test.Helpers._
import play.api.libs.json.Json.{arr, obj}
import models._

class LinkControllerSpec extends AppSpec {

  "POST /api/v1/link" should "be secured" in {
    val result = post("/api/v1/link")
    result.status should equal (UNAUTHORIZED)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Unauthorized")
    ))
  }
  
  it should "return 400 if parameters are missing" in new Fixtures {
    val result = post("/api/v1/link?token=good-token")
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "url" -> arr("This field is required")
      )
    ))
  }
  
  it should "return 400 if parameters are malformed" in new Fixtures {
    val result = post("/api/v1/link?token=good-token", obj(
        "url"      -> "bad-url",
        "code"     -> "b",           // Too short
        "folder_id" -> "not a number"
    ))
    result.status should equal (BAD_REQUEST)
    result.json should equal (obj(
      "errors" -> obj(
        "url"       -> arr("Invalid url"),
        "code"      -> arr("Minimum length is 5"),
        "folder_id" -> arr("Numeric value expected")
      )
    ))
  }
  
  it should "create link if all parameters are valid" in new Fixtures {
    val result = post("/api/v1/link?token=good-token", obj(
        "url"       -> "http://very-long-url.com",
        "code"      -> "spock",
        "folder_id" -> folder.id.get
    ))
    
    result.status should equal (OK)
    
    result.json should equal (obj(
      "url"         -> "http://very-long-url.com",
      "code"        -> "spock",
      "folder_id"   -> folder.id.get,
      "click_count" -> 0
    ))
    
    val newLink = Link.findOneByCode("spock").get
    newLink.url should be ("http://very-long-url.com")
    newLink.code should be ("spock")
    newLink.folderId should be (folder.id.toOption)
    newLink.userId should be (me.id.get)
  }
  
  it should "return 400 if code already exists" in new Fixtures {
    val result = post("/api/v1/link?token=good-token", obj(
        "url"       -> "http://very-long-url.com",
        "code"      -> link_1.code,
        "folder_id" -> folder.id.get
    ))
    
    result.status should equal (BAD_REQUEST)
    
    result.json should equal (obj(
      "errors" -> obj(
        "code"      -> arr("Already exists")
      )
    ))
  }
  
  "GET /api/v1/link/:code" should "be secured" in {
    val result = get("/api/v1/link/whatever")
    result.status should equal (UNAUTHORIZED)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Unauthorized")
    ))
  }
  
  it should "return 404 if no such link exists" in new Fixtures {
    val result = get(s"/api/v1/link/whatever?token=${me.token}")
    
    result.status should equal (NOT_FOUND)
    
    result.json should equal (obj(
      "errors" -> obj(
        "code"      -> arr("Not exists")
      )
    ))
  }
  
  it should "return link" in new Fixtures {
    val result = get(s"/api/v1/link/${link_1.code}?token=${me.token}")
    
    result.status should equal (OK)
    
    result.json should equal (obj(
      "url"         -> link_1.url,
      "code"        -> link_1.code,
      "folder_id"   -> link_1.folderId,
      "click_count" -> link_1.clickCount
    ))
  }
  
  "DELETE /api/v1/link/:code" should "be secured" in {
    val result = delete(s"/api/v1/link/whatever")
    result.status should equal (UNAUTHORIZED)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Unauthorized")
    ))
  }
  
  it should "return 404 if no such folder found" in new Fixtures {
    val result = delete(s"/api/v1/link/whatever?token=${me.token}")
    result.status should equal (NOT_FOUND)
    result.json should equal (obj(
      "errors" -> obj(
        "code" -> arr("Not exists")
      )
    ))
  }
  
  it should "delete link" in new Fixtures {
    val result = delete(s"/api/v1/link/${link_1.code}?token=${me.token}")
    result.status should equal (OK)
    
    Link.findOneByCode(link_1.code) should be ('empty)
  }
  
  "GET /api/v1/link" should "be secured" in {
    val result = get("/api/v1/link")
    result.status should equal (UNAUTHORIZED)
    result.json should equal (obj(
      "errors"         -> obj(),
      "error_messages" -> arr("Unauthorized")
    ))
  }
  
  it should "return my links" in new Fixtures {
    val result = get(s"/api/v1/link?token=${me.token}")
    result.status should equal (OK)
    result.json should equal (arr(
      obj(
        "url"         -> link_1.url,
        "code"        -> link_1.code,
        "folder_id"   -> link_1.folderId,
        "click_count" -> link_1.clickCount
      ), obj(
        "url"         -> link_2.url,
        "code"        -> link_2.code,
        "folder_id"   -> link_2.folderId,
        "click_count" -> link_2.clickCount
      )
    ))
  }
  
  class Fixtures {
  
    val me   = User create User("good-secret", "good-token")
    
    val folder = Folder create Folder("existing", me.id.get)
    
    val link_1 = Link create Link("http://url.com", None, me.id.get, Some(folder.id.get))
    val link_2 = Link create Link("http://other.com", None, me.id.get, Some(folder.id.get))

  }
    
}