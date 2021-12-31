package org.sailcbi.APIServer.Api.Endpoints.Staff.Rest.MembershipSale

import com.coleji.neptune.Core.{ParsedRequest, PermissionsAuthority}
import com.coleji.neptune.Storable.Filter
import com.coleji.neptune.Storable.StorableQuery.ColumnAlias.wrap
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import org.sailcbi.APIServer.Entities.EntityDefinitions.{Event, FoClose, MembershipType, PersonMembership, ProgramType}
import org.sailcbi.APIServer.Entities.dto.MembershipSale
import org.sailcbi.APIServer.UserTypes.StaffRequestCache
import play.api.libs.json.Json
import play.api.mvc.{Action, AnyContent, InjectedController}

import javax.inject.Inject
import scala.concurrent.{ExecutionContext, Future}

class GetMembershipSale @Inject()(implicit val exec: ExecutionContext) extends InjectedController {
	def get(season: Int)(implicit PA: PermissionsAuthority): Action[AnyContent] = Action.async(req => {
		PA.withRequestCache(StaffRequestCache)(None, ParsedRequest(req), rc => {
			val saleClose = FoClose.alias("SALES_CLOSE")
			val voidClose = FoClose.aliasOuter("VOID_CLOSE")

			val qb = QueryBuilder
				.from(PersonMembership)
				.innerJoin(
					MembershipType,
					MembershipType.fields.membershipTypeId equalsField PersonMembership.fields.membershipTypeId
				)
				.innerJoin(
					saleClose,
					saleClose.wrappedFields(_.closeId) equalsField PersonMembership.fields.closeId
				)
				.outerJoin(
					voidClose,
					voidClose.wrappedFields(_.closeId) equalsField PersonMembership.fields.voidCloseId
				)
				.where(PersonMembership.fields.purchaseDate.alias isYearConstant season)
				.select(List(
					PersonMembership.fields.assignId,
					PersonMembership.fields.membershipTypeId,
					MembershipType.fields.programId,
					PersonMembership.fields.discountInstanceId,
					PersonMembership.fields.closeId,
					PersonMembership.fields.voidCloseId,
					PersonMembership.fields.purchaseDate,
					PersonMembership.fields.startDate,
					PersonMembership.fields.expirationDate,
					PersonMembership.fields.price,
					saleClose.wrappedFields(_.closedDatetime),
					voidClose.wrappedFields(_.closeId),
					voidClose.wrappedFields(_.closedDatetime),
				))

			val rows = rc.executeQueryBuilder(qb).map(row => MembershipSale(
				assignId = row.getValue(PersonMembership)(_.assignId),
				membershipTypeId = row.getValue(PersonMembership)(_.membershipTypeId),
				programId = row.getValue(MembershipType)(_.programId),
				discountInstanceId = row.getValue(PersonMembership)(_.discountInstanceId),
				closeId =row.getValue(PersonMembership)(_.closeId),
				voidCloseId = row.getValue(PersonMembership)(_.voidCloseId),
				purchaseDate = row.getValue(PersonMembership)(_.purchaseDate),
				startDate = row.getValue(PersonMembership)(_.startDate),
				expirationDate = row.getValue(PersonMembership)(_.expirationDate),
				price = row.getValue(PersonMembership)(_.price),
				saleClosedDatetime = row.getValue(saleClose)(_.closedDatetime),
				voidClosedDatetime = row.getValue(voidClose)(_.closedDatetime).flatten
			))

			// TODO: paginate?
			Future(Ok(Json.toJson(rows.take(1000))))
		})
	})
}
