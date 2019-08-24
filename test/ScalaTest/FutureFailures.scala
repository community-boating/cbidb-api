package ScalaTest

import org.sailcbi.APIServer.CbiUtil.{Failed, Failover, Rejected, Resolved}
import org.sailcbi.APIServer.IO.HTTP.FromScalajHTTP
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.util.{Failure, Success}

@RunWith(classOf[JUnitRunner])
class FutureFailures extends FunSuite {
  val futureString: Future[String] = Future {
    throw new Exception("Boom!")
  }

  val futureFailover: Future[Failover[String, String]] = futureString.transform({
    case Success(s: String) => Success(Resolved(s))
    case Failure(e: Throwable) => Success(Failed(e))
  })

  test("test") {
    val forAssert = futureFailover.map(s => {
      assert(s.isFailed)
    })
    Await.result(forAssert, Duration(50, "seconds"))
  }
}
