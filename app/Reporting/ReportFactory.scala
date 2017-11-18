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

  val entityCompanion: StorableObject[T]
  // TODO: some sanity check that this can't be more than like 100 things or something
  val getAllFilter: (PersistenceBroker => ReportingFilter[T]) = pb =>
    new ReportingFilterFunction[T](pb, pb => {
      pb.getAllObjectsOfClass(
        entityCompanion
      ).toSet
    })

  val filterList: List[(String, ReportingFilterFactory[T, _])]
  val fieldList: List[(String, ReportingField[T])]

  lazy val filterMap: Map[String, ReportingFilterFactory[T, _]] = filterList.toMap
  lazy val fieldMap: Map[String, ReportingField[T]] = fieldList.toMap

  lazy private val getCombinedFilter: ReportingFilter[T] = {
    val parser: ReportingFilterSpecParser[T] = new ReportingFilterSpecParser[T](pb, filterMap, getAllFilter)
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

  lazy val getFieldsToUse: List[ReportingField[T]] = {
    fieldSpecWrapper.get.split(",").map(name => fieldMap.get(name) match {
      case Some(rf: ReportingField[T]) => rf
      case None => throw new Exception("Couldn't find reporting field " + name)
    }).toList
  }

  def getHeaders: List[String] = getFieldsToUse.map(_.fieldDisplayName)
  def getRows: List[List[String]] = {
    setInstances()
    val valueFunctions: List[ValueFunction] = getFieldsToUse.map(_.valueFunction)
    instances.get.map(i => {
      valueFunctions.map(fn => {
        fn(i)
      })
    })
  }

  protected def decorateInstancesWithParentReferences(instances: List[T]): Unit
}
