package org.sailcbi.APIServer.Reports.ApClassRoster.Model

import com.coleji.neptune.PDFBox.ReportModel

case class ApClassRosterModel(
	rosterData: List[ApRosterData],
	instanceData: ApClassInstanceData
) extends ReportModel