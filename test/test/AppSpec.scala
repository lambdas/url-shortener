package test

import org.scalatest._
import play.api.test.{TestServer, FakeApplication}
import play.api.Logger
import anorm.SqlParser._
import anorm._
import play.api.db.DB
import play.api.Play.current

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
    clearTables()
  }

  after {
    
  }

  protected def clearTables() {
    DB.withConnection { implicit connection =>
      SQL(
        s"""
          |truncate users restart identity cascade;
          |truncate folders restart identity cascade;
        """.stripMargin
      ).executeUpdate()
    }
  }
}