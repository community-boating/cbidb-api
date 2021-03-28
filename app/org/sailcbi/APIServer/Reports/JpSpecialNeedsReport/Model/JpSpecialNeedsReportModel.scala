package org.sailcbi.APIServer.Reports.JpSpecialNeedsReport.Model

import com.coleji.framework.PDFBox.ReportModel

import java.time.ZonedDateTime

case class JpSpecialNeedsReportModel(
	rosterData: List[JpSpecialNeedsData],
	from: ZonedDateTime,
	to: ZonedDateTime
) extends ReportModel