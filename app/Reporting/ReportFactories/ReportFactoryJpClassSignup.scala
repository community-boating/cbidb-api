package Reporting.ReportFactories

import Entities.EntityDefinitions._
import Reporting.ReportingFilters.ReportingFilterFactory
import Reporting.{ReportFactory, ReportingField}
import Storable.StorableObject

class ReportFactoryJpClassSignup extends ReportFactory[JpClassSignup] {
  val entityCompanion: StorableObject[JpClassSignup] = JpClassSignup

  lazy val jpClassTypes: List[JpClassType] = pb.getObjectsByFilters(JpClassType, List(), 20)

  // decorated with types
  lazy val jpClassInstances: List[JpClassInstance] = {
    val classInstanceIDs: List[Int] = getInstances.map(_.values.instanceId.get)
    val classInstances: List[JpClassInstance] = pb.getObjectsByIds(JpClassInstance, classInstanceIDs)
    classInstances.foreach(i => {
      val typeId = i.values.typeId.get
      val typeInstance = jpClassTypes.find(_.values.typeId.get == typeId)
      i.references.jpClassType.set(typeInstance.get)
    })
    classInstances
  }

  def decorateInstancesWithParentReferences(signups: List[JpClassSignup]): Unit = {
    signups.foreach(s => {
      val instance = jpClassInstances.find(_.values.instanceId.get == s.values.instanceId.get)
      s.references.jpClassInstance.set(instance.get)
    })
  }

  val fieldList: List[(String, ReportingField[JpClassSignup])] = List(
    ("SignupID", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.signupId, "Signup ID", isDefault = true)),
    ("InstanceId", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.instanceId, "Instance ID", isDefault = true)),
    ("PersonId", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.personId, "Person ID", isDefault = true)),
    ("SignupType", ReportingField.getReportingFieldFromDatabaseField(JpClassSignup.fields.signupType, "Signup Type", isDefault = true)),

    ("TypeId", ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassSignup, JpClassInstance](
      JpClassInstance.fields.typeId,
      i => i.references.jpClassInstance.get,
      "Type ID",
      isDefault = true
    )),
    ("TypeName", ReportingField.getReportingFieldFromDatabaseFieldParentObject[JpClassSignup, JpClassType](
      JpClassType.fields.typeName,
      i => i.references.jpClassInstance.get.references.jpClassType.get,
      "Type Name",
      isDefault = true
    ))
    /*
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
    ))*/
  )

  val filterList: List[(String, ReportingFilterFactory[JpClassSignup])] = List(
  //  ("ApClassInstanceFilterYear", new ApClassInstanceFilterFactoryYear()),
  //  ("ApClassInstanceFilterType", new ApClassInstanceFilterFactoryType())
  )
}