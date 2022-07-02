package org.sailcbi.APIServer.Reporting.ReportFactories

import com.coleji.neptune.Export.{ReportFactory, ReportingField, ReportingFilterFactory}
import com.coleji.neptune.Storable.StorableObject
import org.sailcbi.APIServer.Entities.EntityDefinitions._
import org.sailcbi.APIServer.Reporting.ReportingFilterFactories.JpClassSignup.JpClassSignupFilterFactoryYear

import java.time.format.DateTimeFormatter

class ReportFactoryJpClassSignup extends ReportFactory[JpClassSignup] {
	val entityCompanion: StorableObject[JpClassSignup] = JpClassSignup

	lazy val jpClassTypes: List[JpClassType] = rc.getObjectsByFilters(JpClassType, List(), Set(JpClassType.primaryKey), 20)

	lazy val classInstanceIDs: Set[Int] = getInstances.map(_.values.instanceId.get).toSet

	// decorated with types
	lazy val jpClassInstances: List[JpClassInstance] = {
		val classInstances: List[JpClassInstance] = rc.getObjectsByIds(JpClassInstance, classInstanceIDs.toList, Set(JpClassInstance.primaryKey))
		classInstances.foreach(i => {
			val typeId = i.values.typeId.get
			val typeInstance = jpClassTypes.find(_.values.typeId.get == typeId)
			i.references.jpClassType.set(typeInstance.get)
		})
		classInstances
	}

	lazy val jpClassSessions: List[JpClassSession] = rc.getObjectsByFilters(
		JpClassSession,
		List(JpClassSession.fields.instanceId.alias.inList(classInstanceIDs.toList)),
		Set(JpClassSession.primaryKey),
		100
	)

	def decorateInstancesWithParentReferences(signups: List[JpClassSignup]): Unit = {
		signups.foreach(s => {
			val instance = jpClassInstances.find(_.values.instanceId.get == s.values.instanceId.get)
			s.references.jpClassInstance.set(instance.get)
			instance.get.calculatedValues.sessions.findAllInCollection(jpClassSessions)
		})
	}

	val fieldList: List[(String, ReportingField[JpClassSignup])] = List(
		("TypeId", ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassSignup, JpClassInstance](
			JpClassInstance.fields.typeId,
			i => i.references.jpClassInstance.get,
			"Type ID",
			isDefault = true
		)),
		("InstanceId", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.instanceId, "Instance ID", isDefault = true)),
		("SignupID", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.signupId, "Signup ID", isDefault = true)),
		("PersonId", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.personId, "Person ID", isDefault = true)),
		("TypeName", ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassSignup, JpClassType](
			JpClassType.fields.typeName,
			i => i.references.jpClassInstance.get.references.jpClassType.get,
			"Type Name",
			isDefault = true
		)),
		("WeekAlias", ReportingField.getReportingFieldFromCalculatedValue[JpClassSignup, JpClassSession](
			(session: JpClassSession) => session.calculatedValues.jpWeekAlias.getWithInput(rc).getOrElse(""),
			(signup: JpClassSignup) => signup.references.jpClassInstance.get.calculatedValues.firstSession,
			"Week",
			isDefault = true
		)),
		("FirstSessionDatetime", ReportingField.getReportingFieldFromCalculatedValue[JpClassSignup, JpClassSession](
			(session: JpClassSession) => session.values.sessionDateTime.get.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
			(signup: JpClassSignup) => signup.references.jpClassInstance.get.calculatedValues.firstSession,
			"First Session Datetime",
			isDefault = true
		)),
		("SignupType", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.signupType, "Signup Type", isDefault = true)),
		("SignupDatetime", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.signupDatetime, "Signup Datetime", isDefault = true))
	)

	val filterList: List[(String, ReportingFilterFactory[JpClassSignup])] = List(
		("JpClassSignupFilterYear", new JpClassSignupFilterFactoryYear())
	)
}