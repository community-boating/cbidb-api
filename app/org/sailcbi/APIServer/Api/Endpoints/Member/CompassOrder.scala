package org.sailcbi.APIServer.Api.Endpoints.Member

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority, RequestCache}
import org.sailcbi.APIServer.Entities.MagicIds.ORDER_NUMBER_APP_ALIAS
import org.sailcbi.APIServer.Entities.dto.Square.{GenericPostWithOrderAppAliasShape, PostPayOrderViaGiftCardShape, PostPayOrderViaPaymentSourceShape}
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.{LockedRequestCacheWithSquareController, MemberMaybeOrProtoPersonRequestCache, MemberMaybeRequestCache, MemberRequestCache, ProtoPersonRequestCache}
import play.api.libs.ws.WSClient
import play.api.mvc.{Action, AnyContent, InjectedController, Result}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

class CompassOrder @Inject()(ws: WSClient)(implicit val exec: ExecutionContext) extends InjectedController  {

  private def getOrderIdByOrderType(orderAppAlias: String, rc: RequestCache, authedPersonId: Int): Int =
        PortalLogic.getOrderId(rc, authedPersonId, orderAppAlias)
  private def getRequestCacheByOrderType(orderAppAlias: String, parsedRequest: ParsedRequest, block: (LockedRequestCacheWithSquareController, Int) => Future[Result])(implicit PA: PermissionsAuthority): Future[Result] = {
    if(List(
      ORDER_NUMBER_APP_ALIAS.AP,
      ORDER_NUMBER_APP_ALIAS.JP
    ).contains(orderAppAlias)) {
      PA.withRequestCache(MemberRequestCache)(None, parsedRequest, rc => block(rc, rc.getAuthedPersonId))
    }else if(List(
      ORDER_NUMBER_APP_ALIAS.GC,
      ORDER_NUMBER_APP_ALIAS.DONATE
    ).contains(orderAppAlias)) {
      MemberMaybeOrProtoPersonRequestCache.getRC(PA, parsedRequest, rc => {
          block(rc, rc.getAuthedPersonId.get)
      })
    }else{
      Future(BadRequest)
    }
  }

  def postUpsertSquareCustomer()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).upsertSquareCustomer(authedPersonId).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postUpsertOrder()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        val orderId = getOrderIdByOrderType(parsed.orderAppAlias, rc, authedPersonId)
          rc.getCompassIOController(ws).upsertCompassOrder(parsed.orderAppAlias, orderId).transform({
            case Success(s) => Success(Ok(s))
            case Failure(f) => Failure(f)
          })
      })
    })

  }



  def postPayOrderFree()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, PostPayOrderViaGiftCardShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        val orderId = getOrderIdByOrderType(parsed.orderAppAlias, rc, authedPersonId)
        rc.getCompassIOController(ws).payCompassOrderFree(orderId).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })
  }



  def postPayOrderViaPaymentSource()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, PostPayOrderViaPaymentSourceShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        val orderId = getOrderIdByOrderType(parsed.orderAppAlias, rc, authedPersonId)
        rc.getCompassIOController(ws).payCompassOrderViaPaymentSource(Some(authedPersonId), orderId, request.body.asJson.map(a => a.toString).orNull).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def getFetchAPIConstants()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).fetchAPIConstants().transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def getStaggeredPaymentInvoices()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).getStaggeredPaymentInvoices(authedPersonId).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postPublishStaggeredPaymentInvoice()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    val requestBodyJson = request.body.asJson.map(a => a.toString()).getOrElse("")
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).publishStaggeredPaymentInvoice(authedPersonId, requestBodyJson).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postStoreCard()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    val requestBodyJson = request.body.asJson.map(a => a.toString()).getOrElse("")
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).storeSquareCard(authedPersonId, requestBodyJson).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postClearStoredCard()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).clearSquareCard(authedPersonId, request.body.asJson.map(a => a.toString).orNull).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def getOrderStatus()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    val requestBodyJson = request.body.asJson.map(a => a.toString()).getOrElse("")
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).pollCompassOrderStatus(requestBodyJson).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }



  def postGetGiftCardInfo()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async { request =>

    val parsedRequest = ParsedRequest(request)
    val requestBodyJson = request.body.asJson.map(a => a.toString()).getOrElse("")
    PA.withParsedPostBodyJSON(request.body.asJson, GenericPostWithOrderAppAliasShape.apply)(parsed => {
      getRequestCacheByOrderType(parsed.orderAppAlias, parsedRequest, (rc, authedPersonId) => {
        rc.getCompassIOController(ws).getSquareGiftCardInfo(authedPersonId, requestBodyJson).transform({
          case Success(s) => Success(Ok(s))
          case Failure(f) => Failure(f)
        })
      })
    })

  }

}
