package Entities

trait CastableToStorableObject[T <: CastableToStorableClass] {
  val apexTableName: String
  protected val persistenceFieldsMap: Map[String, T => String]
  val pkColumnName: String

  val persistenceFields: List[String] = persistenceFieldsMap.toList.map(t => t._1)
  def persistenceValues(t: T): Map[String, String] = persistenceFieldsMap.map(tup => (tup._1, tup._2(t)))
}
