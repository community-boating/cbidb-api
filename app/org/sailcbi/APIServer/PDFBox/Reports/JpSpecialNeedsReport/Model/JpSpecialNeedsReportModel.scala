package org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Model

import java.time.ZonedDateTime

import org.sailcbi.APIServer.PDFBox.ReportModel

case class JpSpecialNeedsReportModel(
	rosterData: List[JpSpecialNeedsData],
	from: ZonedDateTime,
	to: ZonedDateTime
) extends ReportModel