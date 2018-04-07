package PDFBox.Reports.JpSpecialNeedsReport.Loader

import java.time.ZonedDateTime

import PDFBox.ReportParameter

case class JpSpecialNeedsReportLiveParameter(from: ZonedDateTime, to: ZonedDateTime) extends ReportParameter