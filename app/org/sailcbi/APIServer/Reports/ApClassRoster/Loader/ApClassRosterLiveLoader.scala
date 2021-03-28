package org.sailcbi.APIServer.Reports.ApClassRoster.Loader

import com.coleji.framework.Core.RequestCache
import com.coleji.framework.PDFBox.ReportLoader
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.ApClassRoster.{GetApClassInstanceData, GetApClassSignups}
import org.sailcbi.APIServer.Reports.ApClassRoster.Model.{ApClassInstanceData, ApClassRosterModel}

object ApClassRosterLiveLoader extends ReportLoader[ApClassRosterLiveParameter, ApClassRosterModel] {
	override def apply(param: ApClassRosterLiveParameter, rc: RequestCache): ApClassRosterModel = {
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
