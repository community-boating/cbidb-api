package PDFBox.Reports.JpSpecialNeedsReport

import java.awt.Color

import PDFBox.Abstract.AbstractTable
import PDFBox.PDFReport
import PDFBox.Reports.JpSpecialNeedsReport.Model.{JpSpecialNeedsData, JpSpecialNeedsReportModel}
import PDFBox.Reports.JpSpecialNeedsReport.View.JpSpecialNeedsView
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.font.{PDFont, PDType1Font}

class JpSpecialNeedsReport(data: JpSpecialNeedsReportModel) extends PDFReport(data) {
	val defaultFont: PDFont = PDType1Font.HELVETICA
	val defaultBoldFont: PDFont = PDType1Font.HELVETICA_BOLD
	val defaultFontSize: Float = 11f
	val defaultColor: Color = Color.BLACK

	val sideMargin: Float = 30
	val topMargin: Float = 30

	val marginBetweenReports: Float = 10f
	val verticalLimit: Float = PDFReport.MAX_HEIGHT - (2 * topMargin)

	val left = sideMargin
	val top = PDFReport.MAX_HEIGHT - topMargin

	def appendToDocument(document: PDDocument): Unit = {
		val filter: (JpSpecialNeedsData => Boolean) = d =>
			d.allergies.isDefined || d.medications.isDefined || d.specialNeeds.isDefined
		val fullWidthTables = List(
			new JpSpecialNeedsView(data.from, data.to, data.rosterData.filter(filter), defaultBoldFont, defaultFont, defaultFontSize)
		)

		val pageAndRemainingHeight = AbstractTable.doTable(fullWidthTables, List.empty, None, None, left, top, marginBetweenReports, topMargin, verticalLimit, newPage, document)
		pageAndRemainingHeight._1.get.close()
	}
}