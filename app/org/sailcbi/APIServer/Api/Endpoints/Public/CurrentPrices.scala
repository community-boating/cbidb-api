package org.sailcbi.APIServer.Api.Endpoints.Public

import org.sailcbi.APIServer.CbiUtil.ParsedRequest
import org.sailcbi.APIServer.IO.Portal.PortalLogic
import org.sailcbi.APIServer.UserTypes.PublicRequestCache
import org.sailcbi.APIServer.Services.PermissionsAuthority
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class CurrentPrices @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get()(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		val parsedRequest = ParsedRequest(req)
		PA.withRequestCache(PublicRequestCache)(None, parsedRequest, rc => {


			val membershipPrices = PortalLogic.getAllMembershipPrices(rc)
			val (dwPrice, gpPrice) = PortalLogic.getDwGpPrices(rc)
			val discounts = PortalLogic.getAllDiscounts(rc)

			val memberships: List[MembershipsShape] = membershipPrices.map(mp => {
				val (membershipId, membershipBasePrice) = mp
				MembershipsShape(
					membershipId = membershipId,
					membershipBasePrice = membershipBasePrice,
					discounts = discounts.filter(_._1 == membershipId).map(md => {
						val (_, discountId, discountAmt) = md
						DiscountsShape(
							discountId = discountId,
							discountAmt = discountAmt
						)
					})
				)
			})

			implicit val format = CurrentPricesShape.format

			Future(Ok(Json.toJson(CurrentPricesShape(
				memberships = memberships,
				damageWaiverPrice = dwPrice,
				guestPrivsPrice = gpPrice
			))))
		})
	})

	private case class DiscountsShape(
		discountId: Int,
		discountAmt: Double
	)
	private object DiscountsShape {
		implicit val format = Json.format[DiscountsShape]
		def apply(v: JsValue): DiscountsShape = v.as[DiscountsShape]
	}

	private case class MembershipsShape(
		membershipId: Int,
		membershipBasePrice: Double,
		discounts: List[DiscountsShape]
	)
	private object MembershipsShape {
		implicit val format = Json.format[MembershipsShape]
		def apply(v: JsValue): MembershipsShape = v.as[MembershipsShape]
	}

	private case class CurrentPricesShape(
		memberships: List[MembershipsShape],
		guestPrivsPrice: Double,
		damageWaiverPrice: Double
	)
	private object CurrentPricesShape {
		implicit val format = Json.format[CurrentPricesShape]
		def apply(v: JsValue): CurrentPricesShape = v.as[CurrentPricesShape]
	}
}
