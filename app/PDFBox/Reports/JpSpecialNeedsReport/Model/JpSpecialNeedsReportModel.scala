package PDFBox.Reports.JpSpecialNeedsReport.Model

import java.time.ZonedDateTime

import PDFBox.ReportModel

case class JpSpecialNeedsReportModel(
	rosterData: List[JpSpecialNeedsData],
	from: ZonedDateTime,
	to: ZonedDateTime
) extends ReportModel