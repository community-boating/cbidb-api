package Reporting.ReportFactories

import java.time.format.DateTimeFormatter

import Entities.EntityDefinitions._
import Reporting.ReportingFilters.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryJpClassSignup extends ReportFactory[JpClassSignup] {
  val entityCompanion: StorableObject[JpClassSignup] = JpClassSignup

  lazy val jpClassTypes: List[JpClassType] = pb.getObjectsByFilters(JpClassType, List(), 20)

  lazy val classInstanceIDs: Set[Int] = getInstances.map(_.values.instanceId.get).toSet

  // decorated with types
  lazy val jpClassInstances: List[JpClassInstance] = {
    val classInstances: List[JpClassInstance] = pb.getObjectsByIds(JpClassInstance, classInstanceIDs.toList)
    classInstances.foreach(i => {
      val typeId = i.values.typeId.get
      val typeInstance = jpClassTypes.find(_.values.typeId.get == typeId)
      i.references.jpClassType.set(typeInstance.get)
    })
    classInstances
  }

  lazy val jpClassSessions: List[JpClassSession] = pb.getObjectsByFilters(
    JpClassSession,
    List(JpClassSession.fields.instanceId.inList(classInstanceIDs.toList)),
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
  //  ("ApClassInstanceFilterYear", new ApClassInstanceFilterFactoryYear()),
  //  ("ApClassInstanceFilterType", new ApClassInstanceFilterFactoryType())
  )
}