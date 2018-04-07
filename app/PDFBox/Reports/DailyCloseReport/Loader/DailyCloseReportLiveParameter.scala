package PDFBox.Reports.DailyCloseReport.Loader

import PDFBox.ReportParameter

// In the real world, a close ID is all that is necessary to generate a full model for a close report
case class DailyCloseReportLiveParameter(closeId: Int) extends ReportParameter