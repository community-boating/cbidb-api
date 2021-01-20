package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Loader

import org.sailcbi.APIServer.IO.PreparedQueries.Apex.ApClassRoster.{GetApClassInstanceData, GetApClassSignups}
import org.sailcbi.APIServer.PDFBox.ReportLoader
import org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model.{ApClassInstanceData, ApClassRosterModel}
import org.sailcbi.APIServer.Services.RequestCache

object ApClassRosterLiveLoader extends ReportLoader[ApClassRosterLiveParameter, ApClassRosterModel] {
	override def apply(param: ApClassRosterLiveParameter, rc: RequestCache[_]): ApClassRosterModel = {
		val instanceData = rc.executePreparedQueryForSelect(new GetApClassInstanceData(param.instanceId)).head
		val rosterData = rc.executePreparedQueryForSelect(new GetApClassSignups(param.instanceId))
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
