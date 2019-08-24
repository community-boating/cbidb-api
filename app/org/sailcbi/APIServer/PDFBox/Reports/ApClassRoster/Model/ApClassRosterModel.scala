package org.sailcbi.APIServer.PDFBox.Reports.ApClassRoster.Model

import org.sailcbi.APIServer.PDFBox.ReportModel

case class ApClassRosterModel(
	rosterData: List[ApRosterData],
	instanceData: ApClassInstanceData
) extends ReportModel