package ScalaTest

import java.time.ZonedDateTime

import IO.HTTP.FromScalajHTTP
import IO.Stripe.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import IO.Stripe.StripeIOController
import Services.PermissionsAuthority
import org.junit.runner.RunWith
import org.scalatest.FunSuite
import org.scalatest.junit.JUnitRunner

import scala.concurrent.ExecutionContext.Implicits.global

@RunWith(classOf[JUnitRunner])
class UpdateLocalStripeCharges extends FunSuite {
  GetPersistenceBroker { pb =>
    val ioMech = new StripeAPIIOLiveService(
      PermissionsAuthority.stripeURL,
      "sk_test_flEEKTuVTimwbBcOQ8HyxHmd",
      new FromScalajHTTP()
    )
    val dbMech = new StripeDatabaseIOMechanism(pb)
    val controller = new StripeIOController(ioMech, dbMech)
    controller.updateLocalChargesFromAPIForClose(2104, ZonedDateTime.now.minusDays(4), None)
    assert(1 ==1)
  }
}
