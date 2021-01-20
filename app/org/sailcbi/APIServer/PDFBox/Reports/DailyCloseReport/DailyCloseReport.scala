package org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.{PDFont, PDType1Font}
import org.sailcbi.APIServer.CbiUtil.Currency
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.Model.DailyCloseReportModel
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.ChecksARPage.ChecksARPage
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FirstPage.FirstPage
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.FullWidthTables.FullWidthTables
import org.sailcbi.APIServer.PDFBox.Reports.DailyCloseReport.View.ReceiptsPage.ReceiptsPage
import org.sailcbi.APIServer.PDFBox.{ContentStreamDecorator, PDFReport}

import java.awt.Color

class DailyCloseReport(data: DailyCloseReportModel) extends PDFReport(data) {
	val defaultFont: PDFont = PDType1Font.HELVETICA
	val defaultBoldFont: PDFont = PDType1Font.HELVETICA_BOLD
	val defaultFontSize: Float = 13f
	val defaultColor: Color = Color.BLACK

	val sideMargin: Float = 50
	val topMargin: Float = 40

	def appendToDocument(document: PDDocument): Unit = {
		val firstPageStream: ContentStreamDecorator = newPage(document, PDRectangle.LETTER)
		val firstPageSpec = new FirstPage(data, defaultFont, defaultBoldFont, defaultFontSize, defaultColor, firstPageStream.SIDE_MARGIN, PDFReport.MAX_HEIGHT - 40, document, newPage)
		firstPageSpec.draw(firstPageStream)

		val receiptsPageStream: ContentStreamDecorator = newPage(document, PDRectangle.LETTER)
		val receiptsPageSpec = new ReceiptsPage(data, defaultFont, defaultBoldFont, defaultFontSize, defaultColor, receiptsPageStream.SIDE_MARGIN, PDFReport.MAX_HEIGHT - 40)
		receiptsPageSpec.draw(receiptsPageStream)
		receiptsPageStream.close()

		val checksARPageStream: ContentStreamDecorator = newPage(document, PDRectangle.LETTER)
		val checksARPageSpec = new ChecksARPage(data, defaultFont, defaultBoldFont, defaultFontSize - 3, defaultColor, checksARPageStream.SIDE_MARGIN - 10, PDFReport.MAX_HEIGHT - 40)
		checksARPageSpec.draw(checksARPageStream)
		checksARPageStream.close()

		val fwt = new FullWidthTables(data, defaultFont, defaultBoldFont, defaultFontSize - 3, defaultColor, sideMargin - 10, PDFReport.MAX_HEIGHT - topMargin, topMargin)
		fwt.draw(document, newPage)
	}
}

object DailyCloseReport {
	def getMatchString(revenue: Currency, record: Currency): String = {
		if (revenue.cents - record.cents > 0) "OVER by " + (revenue - record).format()
		else if (revenue.cents - record.cents < 0) "UNDER by " + (record - revenue).format()
		else "MATCH!"
	}
}