package org.sailcbi.APIServer.Reports.JpClassRoster.Model

import com.coleji.neptune.PDFBox.ReportModel

case class JpClassRosterModel(
	rosterData: List[JpRosterData],
	instanceData: JpClassInstanceData
) extends ReportModel