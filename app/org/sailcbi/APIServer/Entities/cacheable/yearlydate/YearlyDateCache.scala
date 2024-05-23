package org.sailcbi.APIServer.Entities.cacheable.yearlydate

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.YearlyDate
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.Duration

object YearlyDateCache extends CacheableFactory[YearlyDateCacheKey, List[YearlyDate]] {
  override protected val lifetime: Duration = Duration.ofHours(24)
  override protected def calculateKey(config: YearlyDateCacheKey): String = CacheKeys.yearlyDate(config)
  override protected def serialize(result: List[YearlyDate]): String = Serde.serializeList(result)
  override protected def deseralize(resultString: String): List[YearlyDate] = Serde.deserializeList(resultString)
  override protected def generateResult(rc: RequestCache, config: YearlyDateCacheKey): List[YearlyDate] = {
    val qb = QueryBuilder.from(YearlyDate)

    rc.executeQueryBuilder(qb).map(YearlyDate.construct)//.sortWith((a, b) => a.values.startDatetime.get.isBefore(b.values.startDatetime.get))
  }
}
