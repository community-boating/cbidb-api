package org.sailcbi.APIServer.PDFBox.Reports.JpClassRoster.Model

import org.sailcbi.APIServer.PDFBox.ReportModel

case class JpClassRosterModel(
	rosterData: List[JpRosterData],
	instanceData: JpClassInstanceData
) extends ReportModel