package Reporting.ReportingFilters

import Entities.{ApClassFormat, ApClassInstance, ApClassType}
import Services.PersistenceBroker

class ApClassInstanceFilterType(pb: PersistenceBroker, classTypeID: String) extends ApClassInstanceFilter {
  val instances: Set[ApClassInstance] = {
    val typeID: Int = {
      val ts: List[ApClassType] = pb.getObjectsByFilters(
        ApClassType,
        List(ApClassType.fields.typeId.equalsConstant(classTypeID.toInt)),
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