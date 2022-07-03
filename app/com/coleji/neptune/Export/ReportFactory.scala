package com.coleji.neptune.Export

import com.coleji.neptune.Core.UnlockedRequestCache
import com.coleji.neptune.Storable.{StorableClass, StorableObject}
import com.coleji.neptune.Util.Initializable

import java.time.{LocalDateTime, ZoneId}

abstract class ReportFactory[T <: StorableClass] {
	private val rcWrapper = new Initializable[UnlockedRequestCache]
	private val filterSpecWrapper = new Initializable[String]
	private val fieldSpecWrapper = new Initializable[String]

	def setParameters(rc: UnlockedRequestCache, filterSpec: String, fieldSpec: String): Unit = {
		rcWrapper.set(rc)
		filterSpecWrapper.set(filterSpec)
		fieldSpecWrapper.set(fieldSpec)
	}

	def rc: UnlockedRequestCache = rcWrapper.get

	def filterSpec: String = filterSpecWrapper.get

	def fieldSpec: String = fieldSpecWrapper.get

	type ValueFunction = (T => String)
	implicit val localDateTimeOrdering: Ordering[LocalDateTime] = Ordering.by(
		(d: LocalDateTime) => d.atZone(ZoneId.systemDefault).toInstant.toEpochMilli
	)

	val entityCompanion: StorableObject[T]
	// TODO: some sanity check that this can't be more than like 100 things or something
	val getAllFilter: (UnlockedRequestCache => ReportingFilter[T]) = rc =>
		new ReportingFilterFunction[T](rc, rc => {
			rc.getAllObjectsOfClass(
				entityCompanion,
				Set(entityCompanion.primaryKey)
			).toSet
		})

	val filterList: List[(String, ReportingFilterFactory[T])]
	val fieldList: List[(String, ReportingField[T])]

	lazy val filterMap: Map[String, ReportingFilterFactory[T]] = filterList.toMap
	lazy val fieldMap: Map[String, ReportingField[T]] = fieldList.toMap

	lazy private val getCombinedFilter: ReportingFilter[T] = {
		val parser: ReportingFilterSpecParser[T] = new ReportingFilterSpecParser[T](rc, filterMap, getAllFilter)
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
