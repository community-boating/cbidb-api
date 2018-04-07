package PDFBox.Reports.ApClassRoster.Loader

import IO.PreparedQueries.Apex.ApClassRoster.{GetApClassInstanceData, GetApClassSignups}
import PDFBox.ReportLoader
import PDFBox.Reports.ApClassRoster.Model.{ApClassInstanceData, ApClassRosterModel}
import Services.PersistenceBroker

object ApClassRosterLiveLoader extends ReportLoader[ApClassRosterLiveParameter, ApClassRosterModel]{
  override def apply(param: ApClassRosterLiveParameter, pb: PersistenceBroker): ApClassRosterModel = {
    val instanceData = pb.executePreparedQueryForSelect(new GetApClassInstanceData(param.instanceId)).head
    val rosterData = pb.executePreparedQueryForSelect(new GetApClassSignups(param.instanceId))
    rosterData.foreach(_.numberSessions.set(instanceData.numberSessions))
    ApClassRosterModel(
      rosterData,
      ApClassInstanceData(
        param.instanceId,
        instanceData.typeName,
        instanceData.numberSessions,
        instanceData.firstSessionTime
      )
    )
  }
}
