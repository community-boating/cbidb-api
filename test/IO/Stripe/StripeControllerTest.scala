package IO.Stripe

import CbiUtil.{CriticalError, ServiceRequestResult, Succeeded, Warning}
import Entities.JsFacades.Stripe.{BalanceTransaction, Payout, StripeCastableToStorableObject, StripeError}
import IO.{COMMIT_TYPE_ASSERT_NO_ACTION, COMMIT_TYPE_DO, CommitType}
import IO.HTTP.FromScalajHTTP
import IO.PreparedQueries.Apex.GetLocalStripePayouts
import IO.Stripe.StripeAPIIO.StripeAPIIOLiveService
import IO.Stripe.StripeDatabaseIO.StripeDatabaseIOMechanism
import ScalaTest.GetPersistenceBroker
import Services.{PermissionsAuthority, ServerInstanceProperties}
import org.scalatest.FunSuite

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Await
import scala.concurrent.duration.Duration

class StripeControllerTest  extends FunSuite {
  val stripeURL = PermissionsAuthority.stripeURL
  val secretKey = {
    val serverProps = new ServerInstanceProperties("conf/private/server-properties")
    serverProps.getProperty("StripeAPIKey")
  }

  GetPersistenceBroker { pb =>

    test("Write Payouts to DB") {
      val apiService = new StripeAPIIOLiveService(stripeURL, secretKey, new FromScalajHTTP)
      val dbService = new StripeDatabaseIOMechanism(pb)
      val logger = PermissionsAuthority.logger
      val controller = new StripeIOController(apiService, dbService, logger)
      val firstFuture = controller.updateLocalDBFromStripeForStorable(
        Payout,
        List.empty,
        None,
        getLocalObjectsQuery =
          ((dbService: StripeDatabaseIOMechanism) => dbService.getObjects(Payout, new GetLocalStripePayouts)),
        insertCommitType = COMMIT_TYPE_DO,
        updateCommitType = COMMIT_TYPE_ASSERT_NO_ACTION,
        deleteCommitType = COMMIT_TYPE_ASSERT_NO_ACTION
      )
      firstFuture.onComplete(f => {

        assert(true)
      })
      Await.result(firstFuture, Duration(40, "seconds"))
    }
  }
}