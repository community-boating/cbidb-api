package org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Loader

import java.time.ZonedDateTime

import org.sailcbi.APIServer.PDFBox.ReportParameter

case class JpSpecialNeedsReportLiveParameter(from: ZonedDateTime, to: ZonedDateTime) extends ReportParameter