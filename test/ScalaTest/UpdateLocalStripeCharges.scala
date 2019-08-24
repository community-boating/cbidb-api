package ScalaTest

import java.time.ZonedDateTime

import org.sailcbi.APIServer.IO.HTTP.FromScalajHTTP
import org.sailcbi.APIServer.IO.Stripe.StripeAPIIO.{StripeAPIIOLiveService, StripeAPIIOMechanism}
import org.sailcbi.APIServer.IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import org.sailcbi.APIServer.IO.Stripe.StripeIOController
import org.sailcbi.APIServer.Services.PermissionsAuthority
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
    val controller = new StripeIOController(ioMech, dbMech, PermissionsAuthority.logger)
   // controller.updateLocalChargesFromAPIForClose(2104, ZonedDateTime.now.minusDays(4), None)
    assert(1 ==1)
  }
}
