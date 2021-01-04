package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Loader

import org.sailcbi.APIServer.IO.PreparedQueries.Apex.ApClassRoster.{GetApClassInstanceData, GetApClassSignups}
import org.sailcbi.APIServer.PDFBox.ReportLoader
import org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model.{ApClassInstanceData, ApClassRosterModel}
import org.sailcbi.APIServer.Services.PersistenceBroker

object ApClassRosterLiveLoader extends ReportLoader[ApClassRosterLiveParameter, ApClassRosterModel] {
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
