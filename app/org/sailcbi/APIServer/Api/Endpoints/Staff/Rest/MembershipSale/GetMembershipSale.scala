package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.MembershipSale

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import org.sailcbi.APIServer.Entities.cacheable.MembershipSales.{MembershipSalesCache, MembershipSalesCacheKey}
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetMembershipSale @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(calendarYear: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val rows = MembershipSalesCache.get(rc, MembershipSalesCacheKey(calendarYear))

			// TODO: paginate?
			Future(Ok(Json.toJson(rows)))
		})
	})
}
