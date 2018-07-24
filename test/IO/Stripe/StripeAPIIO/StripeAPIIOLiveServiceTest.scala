package IO.Stripe.StripeAPIIO

import CbiUtil.{Currency, CurrencyFormat}
import IO.HTTP.FromScalajHTTP
import Services.{PermissionsAuthority, ServerInstanceProperties}
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@RunWith(classOf[JUnitRunner])
class StripeAPIIOLiveServiceTest extends FunSuite {
  val stripeURL = PermissionsAuthority.stripeURL
  val secretKey = {
    val serverProps = new ServerInstanceProperties("conf/private/server-properties")
    serverProps.getProperty("StripeAPIKey")
  }

  // Test that getting a list of stripe objects yields the same final list size regardless of fetch size
  // i.e. test that pagination works
  test("Get Charges Pagination") {
    val apiService = new StripeAPIIOLiveService(stripeURL, secretKey, new FromScalajHTTP)
    val firstFuture = apiService.getCharges(None, 50)
    val secondFuture = apiService.getCharges(None, 10)
    firstFuture.onComplete(first => {
      secondFuture.onComplete(second => {
        val firstList = first.getOrElse(List.empty)
        val secondList = second.getOrElse(List.empty)
        assert(firstList.length == secondList.length)
      })
    })
    Await.result(firstFuture, Duration(40, "seconds"))
    Await.result(secondFuture, Duration(40, "seconds"))
  }
}
