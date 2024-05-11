package org.sailcbi.APIServer.Entities.cacheable.yearlydate

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.{YearlyDate, YearlyDateItem}
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.Duration

object YearlyDateAndItemCache extends CacheableFactory[YearlyDateAndItemCacheKey, List[(YearlyDateItem, YearlyDate)]] {
  override protected val lifetime: Duration = Duration.ofSeconds(30)
  override protected def calculateKey(config: YearlyDateAndItemCacheKey): String = CacheKeys.yearlyDateAndItem(config)
  override protected def serialize(result: List[(YearlyDateItem, YearlyDate)]): String = Serde.serializeList(result)
  override protected def deseralize(resultString: String): List[(YearlyDateItem, YearlyDate)] = Serde.deserializeList(resultString)
  override protected def generateResult(rc: RequestCache, config: YearlyDateAndItemCacheKey): List[(YearlyDateItem, YearlyDate)] = {
    val qb = QueryBuilder.from(YearlyDateItem).innerJoin(YearlyDate, YearlyDate.fields.itemId.alias.equalsField(YearlyDateItem.fields.itemId.alias))
      .where(YearlyDate.fields.year.alias equalsConstant config.year)
      .select(List(
        YearlyDate.fields.year.alias,
        YearlyDate.fields.startDate.alias,
        YearlyDate.fields.endDate.alias,
        YearlyDateItem.fields.itemAlias.alias,
        YearlyDateItem.fields.itemDescription.alias,
      ))
    val test = rc.executeQueryBuilder(qb).map(qbrr => (YearlyDateItem.construct(qbrr), YearlyDate.construct(qbrr)))//.sortWith((a, b) => a.values.startDatetime.get.isBefore(b.values.startDatetime.get))
    println(s"testingitnow$test")
    null
  }
}