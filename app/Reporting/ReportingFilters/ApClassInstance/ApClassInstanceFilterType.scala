package Reporting.ReportingFilters.ApClassInstance

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Services.PersistenceBroker

class ApClassInstanceFilterType(pb: PersistenceBroker, classTypeID: Int) extends ApClassInstanceFilter {
  val primaryKeyValues: Set[Int] = {
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

    val is: List[ApClassInstance] = pb.getObjectsByFilters(
      ApClassInstance,
      List(ApClassInstance.fields.formatId.inList(formatIDs)),
      100
    )

    is.map(i => i.values.instanceId.get).toSet
  }
}
