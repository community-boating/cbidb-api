package org.sailcbi.APIServer.Reports.ApClassRoster.Model

import com.coleji.framework.PDFBox.ReportModel

case class ApClassRosterModel(
	rosterData: List[ApRosterData],
	instanceData: ApClassInstanceData
) extends ReportModel