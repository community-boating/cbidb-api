package IO.Stripe.StripeAPIIO

import java.time.ZonedDateTime

import CbiUtil._
import Entities.JsFacades.Stripe._
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
/*
  // Test that getting a list of stripe objects yields the same final list size regardless of fetch size
  // i.e. test that pagination works
  test("Get Charges Pagination") {
    println("here we go")
    val apiService = new StripeAPIIOLiveService(stripeURL, secretKey, new FromScalajHTTP)
    val firstFuture = apiService.getCharges(Some(ZonedDateTime.now.minusWeeks(3)), 50)
    val secondFuture = apiService.getCharges(Some(ZonedDateTime.now.minusWeeks(3)), 10)
    firstFuture.onComplete(first => {
      secondFuture.onComplete(second => {
        val firstList = first.getOrElse(List.empty)
        val secondList = second.getOrElse(List.empty)
        assert(firstList == secondList)
      })
    })
    Await.result(firstFuture, Duration(40, "seconds"))
    Await.result(secondFuture, Duration(40, "seconds"))
  }
*/
  test("Get Balance Transactions") {
    val apiService = new StripeAPIIOLiveService(stripeURL, secretKey, new FromScalajHTTP)
    val firstFuture = apiService.getBalanceTransactions
    firstFuture.onComplete(f => {
      val r: ServiceRequestResult[scala.List[BalanceTransaction], StripeError] = f.get
      r match {
        case Succeeded(l: List[BalanceTransaction]) => {
          val reversed = l.reverse
          reversed.foreach({
            case c: BalanceTransactionCharge => println("Charge\tAmount\t" + c.amount + "\tFee\t" + c.fee + "\tnet\t" + c.net)
            case r: BalanceTransactionRefund => println("Refund\tAmount\t" + r.amount + "\tFee\t" + r.fee + "\tnet\t" + r.net)
            case p: BalanceTransactionPayout => println("Payout\tAmount\t" + p.amount + "\tFee\t" + p.fee + "\tnet\t" + p.net)
          })
        }
        case Warning(s, e) => throw e
        case CriticalError(e) => throw e
      }
      assert(true)
    })
    Await.result(firstFuture, Duration(40, "seconds"))
  }
}
