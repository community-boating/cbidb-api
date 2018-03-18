package ScalaTest

import Services.Emailer.SSMTPEmailer
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Scratchpad extends FunSuite {
  test("someTest") {
    val emailer = new SSMTPEmailer(Some("jon@community-boating.org"))
    emailer.send("jon@community-boating.org", "Some Subject", "Some body!")
    assert(true)
  }


}
