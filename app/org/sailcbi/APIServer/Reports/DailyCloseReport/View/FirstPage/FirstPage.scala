package org.sailcbi.APIServer.Reports.DailyCloseReport.View.FirstPage

import com.coleji.neptune.PDFBox.Drawable.Drawable
import com.coleji.neptune.PDFBox.{ContentStreamDecorator, PDFReport}
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.sailcbi.APIServer.Reports.DailyCloseReport.Model.DailyCloseReportModel

import java.awt.Color

class FirstPage(data: DailyCloseReportModel, defaultFont: PDFont, defaultBoldFont: PDFont, defaultFontSize: Float, defaultColor: Color, left: Float, top: Float, document: PDDocument, newPage: (PDDocument, PDRectangle) => ContentStreamDecorator) extends Drawable {
	def draw(contentStreamDecorator: ContentStreamDecorator): Unit =
		draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float)

	def draw(contentStreamDecorator: ContentStreamDecorator, left: Float, top: Float): Unit = {
		new Title(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left, top)
		new InPersonMatchTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left, top - 40)
		new InPersonDetailTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left, top - 100)
		new OnlineMatchTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left + 230, top - 40)
		new OnlineDetailTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left + 270, top - 100)
		new ARSummaryTable(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left + 270, top - 235)
		new StaffTables(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top)
				.draw(contentStreamDecorator, left, top - 330)
		new Notes(data, PDFReport.MAX_WIDTH, defaultFont, defaultBoldFont, defaultFontSize, left, top, document, newPage)
				.draw(contentStreamDecorator, left, top - 430)
	}
}
