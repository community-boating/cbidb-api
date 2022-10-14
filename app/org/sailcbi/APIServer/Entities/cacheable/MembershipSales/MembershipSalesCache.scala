package org.sailcbi.APIServer.Entities.cacheable.MembershipSales

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Storable.StorableQuery.ColumnAlias.wrap
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.{FoClose, MembershipType, PersonMembership}
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys
import org.sailcbi.APIServer.Entities.dto.MembershipSale

import java.time.Duration

object MembershipSalesCache extends CacheableFactory[MembershipSalesCacheKey, List[MembershipSale]] {
	override protected val lifetime: Duration = Duration.ofHours(24)

	override protected def calculateKey(config: MembershipSalesCacheKey): String = CacheKeys.membershipSales(config)

	override protected def serialize(result: List[MembershipSale]): String = Serde.serializeList(result)

	override protected def deseralize(resultString: String): List[MembershipSale] = Serde.deserializeList(resultString)

	override protected def generateResult(rc: RequestCache, config: MembershipSalesCacheKey): List[MembershipSale] = {
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
			.where(PersonMembership.fields.purchaseDate.alias isYearConstant config.calendarYear)
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

		rc.executeQueryBuilder(qb).map(row => MembershipSale(
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
	}
}
