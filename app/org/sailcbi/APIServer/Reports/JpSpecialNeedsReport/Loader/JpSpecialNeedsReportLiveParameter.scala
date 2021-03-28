package org.sailcbi.APIServer.Reports.JpSpecialNeedsReport.Loader

import com.coleji.framework.PDFBox.ReportParameter

import java.time.ZonedDateTime

case class JpSpecialNeedsReportLiveParameter(from: ZonedDateTime, to: ZonedDateTime) extends ReportParameter