package test

import org.scalatest._
import play.api.test.{TestServer, FakeApplication}
import play.api.Logger

trait AppSpec extends FlatSpec 
    with Matchers
    with BeforeAndAfter
    with BeforeAndAfterAll {

  val testServer = TestServer(3333, FakeApplication())

  override def beforeAll() {
    testServer.start()
    Logger.shutdown()
  }

  override def afterAll() {
    testServer.stop()
  }

  before {
    
  }

  after {
    
  }

}