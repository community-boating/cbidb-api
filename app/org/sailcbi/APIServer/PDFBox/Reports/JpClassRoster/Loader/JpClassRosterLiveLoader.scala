package org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.Loader

import org.sailcbi.APIServer.IO.PreparedQueries.Apex.JpClassRoster.{GetJpClassInstanceData, GetJpClassSignups}
import org.sailcbi.APIServer.PDFBox.ReportLoader
import org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.Model.{JpClassInstanceData, JpClassRosterModel}
import org.sailcbi.APIServer.Services.PersistenceBroker

object JpClassRosterLiveLoader extends ReportLoader[JpClassRosterLiveParameter, JpClassRosterModel] {
	override def apply(param: JpClassRosterLiveParameter, pb: PersistenceBroker): JpClassRosterModel = {
		val instanceData = pb.executePreparedQueryForSelect(new GetJpClassInstanceData(param.instanceId)).head
		val rosterData = pb.executePreparedQueryForSelect(new GetJpClassSignups(param.instanceId))
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