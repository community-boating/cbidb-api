package Reporting.ReportingFilters

import Entities.{ApClassFormat, ApClassInstance, ApClassSession, ApClassType}
import Services.PersistenceBroker

sealed abstract class ApClassInstanceFilter extends ReportingFilter[ApClassInstance]

class ApClassInstanceFilterType(pb: PersistenceBroker, classTypeID: Int) extends ApClassInstanceFilter {
  val instances: Set[ApClassInstance] = {
    val typeID: Int = {
      val ts: List[ApClassType] = pb.getObjectsByFilters(
        ApClassType,
        List(ApClassType.fields.typeId.equalsConstant(classTypeID)),
        5
      )
      if (ts.size != 1) throw new BadReportingFilterArgumentsException("No such ApClassType with ID " + classTypeID)
      ts.head.values.typeId.get
    }

    val formatIDs: List[Int] = {
      val fs: List[ApClassFormat] = pb.getObjectsByFilters(
        ApClassFormat,
        List(ApClassFormat.fields.typeId.equalsConstant(typeID)),
        5
      )
      fs.map(f => f.values.formatId.get)
    }

    pb.getObjectsByFilters(
      ApClassInstance,
      List(ApClassInstance.fields.formatId.inList(formatIDs)),
      100
    ).toSet
  }
}
object ApClassInstanceFilterType {
  val test = "111"
}

class ApClassInstanceFilterYear(pb: PersistenceBroker, year: Int) extends ApClassInstanceFilter {
  implicit val pbClass: Class[_ <: PersistenceBroker] = pb.getClass
  val instances: Set[ApClassInstance] = {
    val ss: List[ApClassSession] = pb.getObjectsByFilters(
      ApClassSession,
      List(ApClassSession.fields.sessionDateTime.isYearConstant(year)),
      1000
    )
    val instanceIDs = ss.map(s => s.values.instanceId.get)

    pb.getObjectsByFilters(
      ApClassInstance,
      List(ApClassInstance.fields.instanceId.inList(instanceIDs)),
      1000
    ).toSet
  }
}
object ApClassInstanceFilterYear {
  val test = "222"
}
