package org.sailcbi.APIServer.Entities.cacheable.yearlydate

import com.coleji.neptune.Core.{CacheableFactory, RequestCache}
import com.coleji.neptune.Storable.StorableQuery.QueryBuilder
import com.coleji.neptune.Util.Serde
import org.sailcbi.APIServer.Entities.EntityDefinitions.YearlyDateItem
import org.sailcbi.APIServer.Entities.cacheable.CacheKeys

import java.time.Duration

object YearlyDateItemCache extends CacheableFactory[YearlyDateItemCacheKey, List[YearlyDateItem]] {
  override protected val lifetime: Duration = Duration.ofHours(24)
  override protected def calculateKey(config: YearlyDateItemCacheKey): String = CacheKeys.yearlyDateItem(config)
  override protected def serialize(result: List[YearlyDateItem]): String = Serde.serializeList(result)
  override protected def deseralize(resultString: String): List[YearlyDateItem] = Serde.deserializeList(resultString)
  override protected def generateResult(rc: RequestCache, config: YearlyDateItemCacheKey): List[YearlyDateItem] = {
    val qb = QueryBuilder.from(YearlyDateItem)
      .where(YearlyDateItem.fields.itemAlias.alias equalsConstant config.itemAlias)
      .select(List(
        YearlyDateItem.fields.itemId.alias,
        YearlyDateItem.fields.itemAlias.alias,
        YearlyDateItem.fields.itemDescription.alias,
        YearlyDateItem.fields.createdBy.alias,
        YearlyDateItem.fields.createdOn.alias,
        YearlyDateItem.fields.alertIfUnset.alias,
        YearlyDateItem.fields.updatedBy.alias,
        YearlyDateItem.fields.updatedOn.alias,
        YearlyDateItem.fields.valIfUndef.alias
      ))
    rc.executeQueryBuilder(qb).map(YearlyDateItem.construct)//.sortWith((a, b) => a.values.startDatetime.get.isBefore(b.values.startDatetime.get))
  }
}