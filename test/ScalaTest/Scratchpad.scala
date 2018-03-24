package ScalaTest

import Services.Emailer.SSMTPEmailer
import Services.Shell.ShellManager
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class Scratchpad extends FunSuite {
  test("someTest") {
    //val emailer = new SSMTPEmailer(Some("jon@community-boating.org"))
   // emailer.send("jon@community-boating.org", "Some Subject", "Some body!\nMore text!  woo!!!\nhere's some \"quoted\" test too\nyeah!")
    assert(true)
  }


}
