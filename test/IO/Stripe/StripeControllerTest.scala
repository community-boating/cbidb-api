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
/*
  test("Write Payouts to DB") {
    GetPersistenceBroker(pb => {
      println(s"startnig test 0: ${Thread.currentThread.getName}")
      val apiService = new StripeAPIIOLiveService(stripeURL, secretKey, new FromScalajHTTP)
      val dbService = new StripeDatabaseIOMechanism(pb)
      val logger = PermissionsAuthority.logger
      val controller = new StripeIOController(apiService, dbService, logger)
      println(s"second: ${Thread.currentThread.getName}")
      println("1")
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
      println("2")
      firstFuture.onComplete(_.map({
        case CriticalError(e) => throw e
        case _ =>assert(true)
      }))
      println("3")
      Await.result(firstFuture, Duration(40, "seconds"))
      println("4")
    })
  }*/
  test("Write BalanceTransactions to DB") {
    GetPersistenceBroker(pb => {
      println(s"startnig test 0: ${Thread.currentThread.getName}")
      val apiService = new StripeAPIIOLiveService(stripeURL, secretKey, new FromScalajHTTP)
      val dbService = new StripeDatabaseIOMechanism(pb)
      val logger = PermissionsAuthority.logger
      val controller = new StripeIOController(apiService, dbService, logger)
      println(s"second: ${Thread.currentThread.getName}")
      println("1")
      val firstFuture = controller.syncBalanceTransactions
      println("2")
      firstFuture.onComplete(_.map({
        case CriticalError(e) => throw e
        case _ => assert(true)
      }))
      println("3")
      Await.result(firstFuture, Duration(40, "seconds"))
      println("4")
    })
  }
}