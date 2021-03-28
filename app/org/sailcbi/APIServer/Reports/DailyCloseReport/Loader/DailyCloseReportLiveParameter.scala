package org.sailcbi.APIServer.Reports.DailyCloseReport.Loader

import com.coleji.framework.PDFBox.ReportParameter

// In the real world, a close ID is all that is necessary to generate a full model for a close report
case class DailyCloseReportLiveParameter(closeId: Int) extends ReportParameter