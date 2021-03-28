package org.sailcbi.APIServer.Reports.JpClassRoster.Model

import com.coleji.framework.PDFBox.ReportModel

case class JpClassRosterModel(
	rosterData: List[JpRosterData],
	instanceData: JpClassInstanceData
) extends ReportModel