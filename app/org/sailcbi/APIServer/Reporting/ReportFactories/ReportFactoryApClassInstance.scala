package org.sailcbi.APIServer.Reporting.ReportFactories

import com.coleji.neptune.Export.{ReportFactory, ReportingField, ReportingFilterFactory}
import com.coleji.neptune.Storable.StorableObject
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Reporting.ReportingFilterFactories.ApClassInstance.{ApClassInstanceFilterFactoryType, ApClassInstanceFilterFactoryYear}

import java.time.format.DateTimeFormatter

class ReportFactoryApClassInstance extends ReportFactory[ApClassInstance] {
	lazy val apClassFormats: List[ApClassFormat] = rc.getObjectsByFilters(ApClassFormat, List(), Set(ApClassFormat.primaryKey), 20)

	lazy val apClassTypes: List[ApClassType] = rc.getObjectsByFilters(ApClassType, List(), Set(ApClassType.primaryKey), 20)

	lazy val apClassSessions: List[ApClassSession] = rc.getObjectsByFilters(
		ApClassSession,
		List(ApClassSession.fields.instanceId.alias.inList(getInstances.map(i => i.values.instanceId.get))),
		Set(ApClassSession.primaryKey),
		1000
	)

	val entityCompanion: StorableObject[ApClassInstance] = ApClassInstance

	def decorateInstancesWithParentReferences(instances: List[ApClassInstance]): Unit = {
		val formats: Map[Int, ApClassFormat] =
			apClassFormats
					.map(f => (f.getID, f))
					.foldLeft(Map(): Map[Int, ApClassFormat])((m: Map[Int, ApClassFormat], x: (Int, ApClassFormat)) => m + x)

		val types: Map[Int, ApClassType] =
			apClassTypes
					.map(f => (f.getID, f))
					.foldLeft(Map(): Map[Int, ApClassType])((m: Map[Int, ApClassType], x: (Int, ApClassType)) => m + x)

		formats.values.foreach(f => {
			val classType = types(f.values.typeId.get)
			f.references.apClassType.set(classType)
		})

		instances.foreach(i => {
			val classFormat = formats(i.values.formatId.get)
			i.references.apClassFormat.set(classFormat)
		})
	}

	val fieldList: List[(String, ReportingField[ApClassInstance])] = List(
		("TypeId", ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassFormat](
			ApClassFormat.fields.typeId,
			i => i.references.apClassFormat.get,
			"Type ID",
			isDefault = true
		)),
		("TypeName", ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassType](
			ApClassType.fields.typeName,
			i => i.references.apClassFormat.get.references.apClassType.get,
			"Type Name",
			isDefault = true
		)),
		("InstanceId", ReportingField.getReportingFieldFromDatabaseField(ApClassInstance.fields.instanceId, "Instance ID", isDefault = true)),
		("FirstSessionDatetime", new ReportingField[ApClassInstance](
			(i: ApClassInstance) =>
				apClassSessions
						.filter(_.values.instanceId.get == i.getID)
						.map(_.values.sessionDateTime.get)
						.min
						.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
			"First Session Datetime",
			isDefault = true
		)),
		("SessionCt", new ReportingField[ApClassInstance](
			(i: ApClassInstance) => apClassSessions.count(s => s.values.instanceId.get == i.values.instanceId.get).toString,
			"# Sessions",
			isDefault = false
		))
		/*, ("TypeDisplayOrder", ReportingField.getReportingFieldFromDatabaseFieldParentObject[ApClassInstance, ApClassType](
		  ApClassType.fields.displayOrder,
		  i => i.references.apClassFormat.get.references.apClassType.get,
		  "Type DisplayOrder",
		  isDefault = false
		))*/
	)

	val filterList: List[(String, ReportingFilterFactory[ApClassInstance])] = List(
		("ApClassInstanceFilterYear", new ApClassInstanceFilterFactoryYear()),
		("ApClassInstanceFilterType", new ApClassInstanceFilterFactoryType())
	)
}