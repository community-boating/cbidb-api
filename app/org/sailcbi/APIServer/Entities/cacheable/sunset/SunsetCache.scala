package org.sailcbi.APIServer.Entities.cacheable.sunset

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Storable.Filter
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.SunsetTime
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.{Duration, LocalDate}

object SunsetCache extends CacheableFactory[SunsetCacheKey, List[SunsetTime]] {
	override protected val lifetime: Duration = Duration.ofHours(24)
	override protected def calculateKey(config: SunsetCacheKey): String = CacheKeys.sunset(config)
	override protected def serialize(result: List[SunsetTime]): String = Serde.serializeList(result)
	override protected def deseralize(resultString: String): List[SunsetTime] = Serde.deserializeList(resultString)
	def makeWhere(config: SunsetCacheKey): List[Filter] = {
		if(config.day.isEmpty) {
			List(
			SunsetTime.fields.forDate.alias.greaterEqualConstant(LocalDate.of(config.year, config.month, 1)),
			SunsetTime.fields.forDate.alias.lessThanConstant(LocalDate.of(config.year, config.month, 1).plusMonths(1)),
		)
		} else {
			List(
				SunsetTime.fields.forDate.alias.isDateConstant(LocalDate.of(config.year, config.month, config.day.get))
			)
		}

	}
	override protected def generateResult(rc: RequestCache, config: SunsetCacheKey): List[SunsetTime] = {
		val qb = QueryBuilder.from(SunsetTime)
			.where(makeWhere(config))
			.select(List(
				SunsetTime.fields.forDate,
				SunsetTime.fields.twilightStart,
				SunsetTime.fields.sunrise,
				SunsetTime.fields.sunset,
				SunsetTime.fields.twilightEnd,
				SunsetTime.fields.dayLengthSeconds,
				SunsetTime.fields.sonarNoon,
				SunsetTime.fields.nauticalTwilightStart,
				SunsetTime.fields.nauticalTwilightEnd,
				SunsetTime.fields.astronomicalTwilightStart,
				SunsetTime.fields.astronomicalTwilightEnd
			))

		rc.executeQueryBuilder(qb).map(SunsetTime.construct).sortWith((a, b) => a.values.forDate.get.isBefore(b.values.forDate.get))
	}
}