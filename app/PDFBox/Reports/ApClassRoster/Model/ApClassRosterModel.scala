package PDFBox.Reports.ApClassRoster.Model

import PDFBox.ReportModel

case class ApClassRosterModel(
	rosterData: List[ApRosterData],
	instanceData: ApClassInstanceData
) extends ReportModel