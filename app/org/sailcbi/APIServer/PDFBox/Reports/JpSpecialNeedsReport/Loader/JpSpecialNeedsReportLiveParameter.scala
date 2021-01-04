package org.sailcbi.APIServer.PDFBox.Reports.JpSpecialNeedsReport.Loader

import org.sailcbi.APIServer.PDFBox.ReportParameter

import java.time.ZonedDateTime

case class JpSpecialNeedsReportLiveParameter(from: ZonedDateTime, to: ZonedDateTime) extends ReportParameter