package org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Loader

import org.sailcbi.APIServer.IO.PreparedQueries.Apex.JpSpecialNeedsReport.GetSpecialNeedsData
import org.sailcbi.APIServer.PDFBox.ReportLoader
import org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Model.JpSpecialNeedsReportModel
import org.sailcbi.APIServer.Services.PersistenceBroker

object JpSpecialNeedsReportLiveLoader extends ReportLoader[JpSpecialNeedsReportLiveParameter, JpSpecialNeedsReportModel] {
	override def apply(param: JpSpecialNeedsReportLiveParameter, pb: PersistenceBroker[_]): JpSpecialNeedsReportModel = {
		val specNeedsData = pb.executePreparedQueryForSelect(new GetSpecialNeedsData(param.from, param.to))

		JpSpecialNeedsReportModel(
			specNeedsData,
			param.from,
			param.to
		)
	}
}
