package main
import IO.HTTP.{FromScalajHTTP, GET}
import IO.StripeIO
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner
import play.api.libs.json.{JsArray, JsObject}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

@RunWith(classOf[JUnitRunner])
class GetCharges extends FunSuite {
  val sio = new StripeIO(
    "https://api.stripe.com/v1/",
    "",
    new FromScalajHTTP()
  )

  test("Charges") {
    val charges = sio.getCharges(None, 100)
    val result = charges.map(r => {
      assert(r.length == 35)
    })
    Await.result(result, Duration(50, "seconds"))
  }
}
