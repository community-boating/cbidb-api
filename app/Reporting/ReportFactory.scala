package Reporting

import java.time.{LocalDateTime, ZoneId}

import Reporting.ReportingFilters.ReportingFilterFactories.ReportingFilterFactory
import Reporting.ReportingFilters.{ReportingFilter, ReportingFilterFunction, ReportingFilterSpecParser}
import Services.PersistenceBroker
import Storable.{StorableClass, StorableObject}

abstract class ReportFactory[T <: StorableClass] {
  private var pbWrapper: Option[PersistenceBroker] = None
  private var filterSpecWrapper: Option[String] = None
  private var fieldSpecWrapper: Option[String] = None
  def setParameters(pb: PersistenceBroker, filterSpec: String, fieldSpec: String): Unit = {
    pbWrapper = Some(pb)
    filterSpecWrapper = Some(filterSpec)
    fieldSpecWrapper = Some(fieldSpec)
  }
  implicit def pb: PersistenceBroker = pbWrapper match {
    case Some(x: PersistenceBroker) => x
    case None => throw new Exception("Referenced ReportFactory params before they were set")
  }

  def filterSpec: String = filterSpecWrapper match {
    case Some(x: String) => x
    case None => throw new Exception("Referenced ReportFactory params before they were set")
  }
  def fieldSpec: String = fieldSpecWrapper match {
    case Some(x: String) => x
    case None => throw new Exception("Referenced ReportFactory params before they were set")
  }

  type ValueFunction = (T => String)
  implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(
    (d: LocalDateTime) => d.atZone(ZoneId.systemDefault).toInstant.toEpochMilli
  )

  val FIELD_MAP: Map[String, ReportingField[T]]
  val FILTER_MAP: Map[String, ReportingFilterFactory[T, _]]

  val entityCompanion: StorableObject[T]
  // TODO: some sanity check that this can't be more than like 100 things or something
  val getAllFilter: (PersistenceBroker => ReportingFilter[T]) = pb =>
    new ReportingFilterFunction[T](pb, pb => {
      pb.getAllObjectsOfClass(
        entityCompanion
      ).toSet
    })

  lazy val getFields: List[ReportingField[T]] = {
    val FIELD_SEPARATOR: Char = ','
    fieldSpec.split(FIELD_SEPARATOR).toList.map(FIELD_MAP(_))
  }

  lazy private val getCombinedFilter: ReportingFilter[T] = {
    val parser: ReportingFilterSpecParser[T] = new ReportingFilterSpecParser[T](pb, FILTER_MAP, getAllFilter)
    parser.parse(filterSpec)
  }

  private var instances: Option[List[T]] = None

  private def setInstances(): Unit = instances match {
    case None =>
      instances = Some(getCombinedFilter.instances.asInstanceOf[Set[T]].toList)
      decorateInstancesWithParentReferences(instances.get)
    case Some(_) =>
  }

  def getInstances: List[T] = instances match {
    case Some(is: List[T]) => is
    case None => throw new Exception("Tried to get instances before they were set")
  }

  def getReportText: String = {
    setInstances()
    val valueFunctions: List[ValueFunction] = getFields.map(f => f.valueFunction)

    getFields.map(f => f.fieldDisplayName).mkString("\t") +
      "\n" +
      instances.get.map(i => {
        valueFunctions.map(fn => {
          fn(i)
        }).mkString("\t")
      }).mkString("\n")
  }

  protected def decorateInstancesWithParentReferences(instances: List[T]): Unit
}
