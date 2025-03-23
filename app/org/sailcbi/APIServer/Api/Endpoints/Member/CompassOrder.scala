package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.Entities.dto.Square.{PostPayOrderViaGiftCardShape, PostPayOrderViaPaymentSourceShape, PostUpsertOrderShape}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.MemberRequestCache
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

class CompassOrder @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController  {



  private def getOrderIdByOrderType(orderAppAlias: String, rc: MemberRequestCache): Int = {

    if(List(
      ORDER_NUMBER_APP_ALIAS.AP,
      ORDER_NUMBER_APP_ALIAS.JP,
      ORDER_NUMBER_APP_ALIAS.GC,
      ORDER_NUMBER_APP_ALIAS.DONATE
    ).contains(orderAppAlias)) {
        PortalLogic.getOrderId(rc, rc.getAuthedPersonId, orderAppAlias)
    }else {
      throw new RuntimeException("Invalid order app alias supplied")
    }

  }

  def postUpsertSquareCustomer()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)

    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
      rc.getCompassIOController(ws).upsertSquareCustomer(rc.getAuthedPersonId).transform({
        case Success(s) => Success(Ok(s))
        case Failure(f) => Failure(f)
      })
    })

  }



  def postUpsertOrder()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, PostUpsertOrderShape.apply)(parsed => {
      PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
        val orderId = getOrderIdByOrderType(parsed.orderAppAlias, rc)
        rc.getCompassIOController(ws).upsertCompassOrder(orderId).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postPayOrderViaGiftCard()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, PostPayOrderViaGiftCardShape.apply)(parsed => {
      PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
        val orderId = getOrderIdByOrderType(parsed.orderAppAlias, rc)
        rc.getCompassIOController(ws).payCompassOrderViaGiftCard(orderId, parsed.gan).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postPayOrderViaPaymentSource()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, PostPayOrderViaPaymentSourceShape.apply)(parsed => {
      PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
        val orderId = getOrderIdByOrderType(parsed.orderAppAlias, rc)
        rc.getCompassIOController(ws).payCompassOrderViaPaymentSource(orderId, parsed.paymentSourceId).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def getFetchAPIConstants()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => {
      rc.getCompassIOController(ws).fetchAPIConstants().transform({
        case Success(s) => Success(Ok(s))
        case Failure(f) => Failure(f)
      })
    })

  }

}
