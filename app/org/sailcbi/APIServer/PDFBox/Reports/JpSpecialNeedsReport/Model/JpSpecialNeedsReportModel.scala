package org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Model

import org.sailcbi.APIServer.PDFBox.ReportModel

import java.time.ZonedDateTime

case class JpSpecialNeedsReportModel(
	rosterData: List[JpSpecialNeedsData],
	from: ZonedDateTime,
	to: ZonedDateTime
) extends ReportModel