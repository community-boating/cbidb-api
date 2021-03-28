package org.sailcbi.APIServer.Reports.JpClassRoster.Loader

import com.coleji.framework.Core.RequestCache
import com.coleji.framework.PDFBox.ReportLoader
import org.sailcbi.APIServer.IO.PreparedQueries.Apex.JpClassRoster.{GetJpClassInstanceData, GetJpClassSignups}
import org.sailcbi.APIServer.Reports.JpClassRoster.Model.{JpClassInstanceData, JpClassRosterModel}

object JpClassRosterLiveLoader extends ReportLoader[JpClassRosterLiveParameter, JpClassRosterModel] {
	override def apply(param: JpClassRosterLiveParameter, rc: RequestCache): JpClassRosterModel = {
		val instanceData = rc.executePreparedQueryForSelect(new GetJpClassInstanceData(param.instanceId)).head
		val rosterData = rc.executePreparedQueryForSelect(new GetJpClassSignups(param.instanceId))
		rosterData.foreach(_.numberSessions.set(instanceData.numberSessions))
		JpClassRosterModel(
			rosterData,
			JpClassInstanceData(
				param.instanceId,
				instanceData.typeName,
				instanceData.numberSessions,
				instanceData.firstSessionTime
			)
		)
	}
}
