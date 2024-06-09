package org.sailcbi.APIServer.Entities.cacheable.DatetimeRange

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Storable.Filter
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.DatetimeRange
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys
import java.time.Duration

object DatetimeRangeCache extends CacheableFactory[DatetimeRangeCacheKey, List[DatetimeRange]] {
  override protected val lifetime: Duration = Duration.ofHours(24)
  override protected def calculateKey(config: DatetimeRangeCacheKey): String = CacheKeys.datetimeRange(config)
  override protected def serialize(result: List[DatetimeRange]): String = Serde.serializeList(result)
  override protected def deseralize(resultString: String): List[DatetimeRange] = Serde.deserializeList(resultString)
  override protected def generateResult(rc: RequestCache, config: DatetimeRangeCacheKey): List[DatetimeRange] = {
    val qb = QueryBuilder.from(DatetimeRange)
      .where(List(
        DatetimeRange.fields.rangeType.alias.equalsConstant(config.rangeType),
        Filter.or(List(
          Filter.and(List(DatetimeRange.fields.startDatetime.alias.greaterEqualConstant(config.startDate),
            DatetimeRange.fields.startDatetime.alias.lessEqualConstant(config.endDate))),
          Filter.and(List(DatetimeRange.fields.endDatetime.alias.greaterEqualConstant(config.startDate),
            DatetimeRange.fields.endDatetime.alias.lessEqualConstant(config.endDate))),
          Filter.and(List(DatetimeRange.fields.startDatetime.alias.lessEqualConstant(config.startDate),
            DatetimeRange.fields.endDatetime.alias.greaterEqualConstant(config.endDate)))
        ))
      ))
      .select(List(
        DatetimeRange.fields.rangeId,
        DatetimeRange.fields.startDatetime,
        DatetimeRange.fields.endDatetime,
        DatetimeRange.fields.rangeType
      ))
    rc.executeQueryBuilder(qb).map(DatetimeRange.construct).sortWith((a, b) => a.values.startDatetime.get.isBefore(b.values.startDatetime.get))
  }
}